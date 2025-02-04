const std = @import("std");

const main = @import("root");
const graphics = main.graphics;
const draw = graphics.draw;
const Texture = graphics.Texture;
const Vec2f = main.vec.Vec2f;

const gui = @import("../gui.zig");
const GuiWindow = gui.GuiWindow;
const GuiComponent = gui.GuiComponent;

pub var window = GuiWindow {
	.relativePosition = .{
		.{ .attachedToFrame = .{.selfAttachmentPoint = .lower, .otherAttachmentPoint = .lower} },
		.{ .attachedToFrame = .{.selfAttachmentPoint = .lower, .otherAttachmentPoint = .lower} },
	},
	.contentSize = Vec2f{128, 16},
	.id = "debug",
	.isHud = false,
	.showTitleBar = false,
	.hasBackground = false,
	.hideIfMouseIsGrabbed = false,
};

pub fn render() void {
	draw.setColor(0xffffffff);
	var y: f32 = 0;
	draw.print("    fps: {d:.0} Hz{s}", .{1.0/main.lastFrameTime.load(.Monotonic), if(main.settings.vsync) @as([]const u8, " (vsync)") else ""}, 0, y, 8, .left);
	y += 8;
	draw.print("    frameTime: {d:.1} ms", .{main.lastFrameTime.load(.Monotonic)*1000.0}, 0, y, 8, .left);
	y += 8;
	draw.print("window size: {}×{}", .{main.Window.width, main.Window.height}, 0, y, 8, .left);
	y += 8;
	if (main.game.world != null) {
		draw.print("Pos: {d:.1}", .{main.game.Player.getPosBlocking()}, 0, y, 8, .left);
		y += 8;
		draw.print("Game Time: {}", .{main.game.world.?.gameTime.load(.Monotonic)}, 0, y, 8, .left);
		y += 8;
		draw.print("Queue size: {}", .{main.threadPool.queueSize()}, 0, y, 8, .left);
		y += 8;
		draw.print("Mesh Queue size: {}", .{main.renderer.mesh_storage.updatableList.items.len}, 0, y, 8, .left);
		y += 8;
		{
			const faceDataSize: usize = @sizeOf(main.renderer.chunk_meshing.FaceData);
			const size: usize = main.renderer.chunk_meshing.faceBuffer.capacity*faceDataSize;
			const used: usize = main.renderer.chunk_meshing.faceBuffer.used*faceDataSize;
			var largestFreeBlock: usize = 0;
			for(main.renderer.chunk_meshing.faceBuffer.freeBlocks.items) |freeBlock| {
				largestFreeBlock = @max(largestFreeBlock, freeBlock.len);
			}
			const fragmentation = size - used - largestFreeBlock*faceDataSize;
			draw.print("ChunkMesh memory: {} MiB / {} MiB (fragmentation: {} MiB)", .{used >> 20, size >> 20, fragmentation >> 20}, 0, y, 8, .left);
			y += 8;
		}
		draw.print("Biome: {s}", .{main.game.world.?.playerBiome.load(.Monotonic).id}, 0, y, 8, .left);
		y += 8;
		draw.print("Opaque faces: {}, Transparent faces: {}", .{main.renderer.chunk_meshing.quadsDrawn, main.renderer.chunk_meshing.transparentQuadsDrawn}, 0, y, 8, .left);
		y += 8;
	}
}