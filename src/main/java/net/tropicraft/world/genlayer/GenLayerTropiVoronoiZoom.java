package net.tropicraft.world.genlayer;

import net.minecraft.world.gen.layer.IntCache;

public class GenLayerTropiVoronoiZoom extends GenLayerTropicraft {

	public enum Mode {
		CARTESIAN, MANHATTAN;
	}

	public Mode zoomMode;

    public GenLayerTropiVoronoiZoom(long seed, GenLayerTropicraft parent, Mode zoomMode)
    {
        super(seed);
        super.parent = parent;
        this.zoomMode = zoomMode;
        this.setZoom(1);
    }

    /**
     * Returns a list of integer values generated by this layer. These may be interpreted as temperatures, rainfall
     * amounts, or biomeList[] indices based on the particular GenLayer subclass.
     */
    public int[] getInts(int x, int y, int width, int length)
    {
    	final int randomResolution = 1024;
    	final double half = 0.5D;
    	final double almostTileSize = 3.6D;
    	final double tileSize = 4D;
    	
        x -= 2;
        y -= 2;
        int scaledX = x >> 2;
        int scaledY = y >> 2;
        int scaledWidth = (width >> 2) + 2;
        int scaledLength = (length >> 2) + 2;
        int[] parentValues = this.parent.getInts(scaledX, scaledY, scaledWidth, scaledLength);
        int bitshiftedWidth = scaledWidth - 1 << 2; 
        int bitshiftedLength = scaledLength - 1 << 2;
        int[] aint1 = IntCache.getIntCache(bitshiftedWidth * bitshiftedLength);
        int i;

        for(int j = 0; j < scaledLength - 1; ++j) {
            i = 0;
            int baseValue = parentValues[i + 0 + (j + 0) * scaledWidth];

            for(int advancedValueJ = parentValues[i + 0 + (j + 1) * scaledWidth]; i < scaledWidth - 1; ++i) {
                this.initChunkSeed((long)(i + scaledX << 2), (long)(j + scaledY << 2));
                double offsetY = ((double)this.nextInt(randomResolution) / randomResolution - half) * almostTileSize;
                double offsetX = ((double)this.nextInt(randomResolution) / randomResolution - half) * almostTileSize;
                this.initChunkSeed((long)(i + scaledX + 1 << 2), (long)(j + scaledY << 2));
                double offsetYY = ((double)this.nextInt(randomResolution) / randomResolution - half) * almostTileSize + tileSize;
                double offsetXY = ((double)this.nextInt(randomResolution) / randomResolution - half) * almostTileSize;
                this.initChunkSeed((long)(i + scaledX << 2), (long)(j + scaledY + 1 << 2));
                double offsetYX = ((double)this.nextInt(randomResolution) / randomResolution - half) * almostTileSize;
                double offsetXX = ((double)this.nextInt(randomResolution) / randomResolution - half) * almostTileSize + tileSize;
                this.initChunkSeed((long)(i + scaledX + 1 << 2), (long)(j + scaledY + 1 << 2));
                double offsetYXY = ((double)this.nextInt(randomResolution) / randomResolution - half) * almostTileSize + tileSize;
                double offsetXXY = ((double)this.nextInt(randomResolution) / randomResolution - half) * almostTileSize + tileSize;
                
                int advancedValueI = parentValues[i + 1 + (j + 0) * scaledWidth] & 255;
                int advancedValueIJ = parentValues[i + 1 + (j + 1) * scaledWidth] & 255;

                for(int innerX = 0; innerX < 4; ++innerX) {
                    int index = ((j << 2) + innerX) * bitshiftedWidth + (i << 2);

                    for(int innerY = 0; innerY < 4; ++innerY) {
                    	double baseDistance;
                    	double distanceY;
                    	double distanceX;
                    	double distanceXY;
                    	switch(zoomMode) {
                    		/*case CARTESIAN: //FB: DB_DUPLICATE_SWITCH_CLAUSES
                                baseDistance = ((double)innerX - offsetX) * ((double)innerX - offsetX) + ((double)innerY - offsetY) * ((double)innerY - offsetY);
                                distanceY = ((double)innerX - offsetXY) * ((double)innerX - offsetXY) + ((double)innerY - offsetYY) * ((double)innerY - offsetYY);
                                distanceX = ((double)innerX - offsetXX) * ((double)innerX - offsetXX) + ((double)innerY - offsetYX) * ((double)innerY - offsetYX);
                                distanceXY = ((double)innerX - offsetXXY) * ((double)innerX - offsetXXY) + ((double)innerY - offsetYXY) * ((double)innerY - offsetYXY);
                                break;*/
                    		case MANHATTAN:
                            	baseDistance = Math.abs(innerX - offsetX) + Math.abs(innerY - offsetY);
                            	distanceY = Math.abs(innerX - offsetXY) + Math.abs(innerY - offsetYY);
                            	distanceX = Math.abs(innerX - offsetXX) + Math.abs(innerY - offsetYX);
                            	distanceXY = Math.abs(innerX - offsetXXY) + Math.abs(innerY - offsetYXY);
                    			break;
                    		default: 
                                baseDistance = ((double)innerX - offsetX) * ((double)innerX - offsetX) + ((double)innerY - offsetY) * ((double)innerY - offsetY);
                                distanceY = ((double)innerX - offsetXY) * ((double)innerX - offsetXY) + ((double)innerY - offsetYY) * ((double)innerY - offsetYY);
                                distanceX = ((double)innerX - offsetXX) * ((double)innerX - offsetXX) + ((double)innerY - offsetYX) * ((double)innerY - offsetYX);
                                distanceXY = ((double)innerX - offsetXXY) * ((double)innerX - offsetXXY) + ((double)innerY - offsetYXY) * ((double)innerY - offsetYXY);
                    	}
                    	
                        if(baseDistance < distanceY && baseDistance < distanceX && baseDistance < distanceXY) {
                            aint1[index++] = baseValue;
                        } else if(distanceY < baseDistance && distanceY < distanceX && distanceY < distanceXY) {
                            aint1[index++] = advancedValueI;
                        } else if(distanceX < baseDistance && distanceX < distanceY && distanceX < distanceXY) {
                            aint1[index++] = advancedValueJ;
                        } else {
                            aint1[index++] = advancedValueIJ;
                        }
                    }
                }

                baseValue = advancedValueI;
                advancedValueJ = advancedValueIJ;
            }
        }

        int[] aint2 = IntCache.getIntCache(width * length);

        for(i = 0; i < length; ++i) {
            System.arraycopy(aint1, (i + (y & 3)) * bitshiftedWidth + (x & 3), aint2, i * width, width);
        }

        return aint2;
    }
    
    @Override
    public void setZoom(int zoom) {
    	this.zoom = zoom;
    	parent.setZoom(zoom * 4);
    }
    
}