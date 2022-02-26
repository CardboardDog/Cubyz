package cubyz.world.terrain.generators;

import java.util.Random;

import cubyz.api.CurrentWorldRegistries;
import cubyz.api.Resource;
import cubyz.utils.json.JsonObject;
import cubyz.world.Chunk;
import cubyz.world.ChunkManager;
import cubyz.world.blocks.Blocks;
import cubyz.world.terrain.CaveMap;
import cubyz.world.terrain.MapFragment;

/**
 * Generates the basic terrain(stone, dirt, sand, ...).
 */

public class TerrainGenerator implements Generator {
	
	private int stone;
	private int water;

	@Override
	public void init(JsonObject parameters, CurrentWorldRegistries registries) {
		water = Blocks.getByID("cubyz:water");
		stone = Blocks.getByID("cubyz:stone");
	}
	
	@Override
	public int getPriority() {
		return 1024; // Within Cubyz the first to be executed, but mods might want to come before that for whatever reason.
	}
	
	@Override
	public Resource getRegistryID() {
		return new Resource("cubyz", "lifeland_terrain");
	}

	@Override
	public void generate(long seed, int wx, int wy, int wz, Chunk chunk, CaveMap caveMap, MapFragment map, ChunkManager generator) {
		Random rand = new Random(seed);
		int seedX = rand.nextInt() | 1;
		int seedY = rand.nextInt() | 1;
		int seedZ = rand.nextInt() | 1;
		for(int x = 0; x < chunk.getWidth(); x += chunk.voxelSize) {
			for(int z = 0; z < chunk.getWidth(); z += chunk.voxelSize) {
				int heightData = caveMap.getHeightData(x, z);
				boolean makeSurfaceStructure = true;
				for(int y = chunk.getWidth() - chunk.voxelSize; y >= 0; y -= chunk.voxelSize) {
					int mask = 1 << y/chunk.voxelSize;
					if((heightData & mask) != 0) {
						if(makeSurfaceStructure) {
							int surfaceBlock = caveMap.findTerrainChangeAbove(x, z, y) - chunk.voxelSize;
							rand.setSeed((seedX*(wx + x) << 32) ^ seedY*(wy + y) ^ seedZ*(wz + z));
							// Add the biomes surface structure:
							y = Math.min(y + chunk.voxelSize, map.getBiome(wx + x, wz + z).struct.addSubTerranian(chunk, surfaceBlock, caveMap.findTerrainChangeBelow(x, z, surfaceBlock), x, z, rand));
							makeSurfaceStructure = false;
						} else {
							chunk.updateBlockInGeneration(x, y, z, stone);
						}
					} else {
						if(y + wy < 0 && y + wy >= (int)map.getHeight(x + wx, z + wz) - (chunk.voxelSize - 1)) {
							chunk.updateBlockInGeneration(x, y, z, water);
						} else {
							chunk.updateBlockInGeneration(x, y, z, 0);
						}
						makeSurfaceStructure = true;
					}
				}
			}
		}
	}

	@Override
	public long getGeneratorSeed() {
		return 0x65c7f9fdc0641f94L;
	}
}
