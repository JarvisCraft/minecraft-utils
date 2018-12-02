package ru.progrm_jarvis.minecraft.commons.block;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import java.util.*;

@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class SnakyBlockChain<P extends Plugin> implements BlocksChain<P> {

    @NonNull final P plugin;
    @NonNull final @Getter World world;
    @NonNull final @Getter Block initialBlock;

    /**
     * The current layer of blocks to visit
     */
    @NonNull Queue<Block> blocks;

    /**
     * The next layer of blocks to visit
     */
    @NonNull Queue<Block> nextBlocks;

    /**
     * Locations of those blocks that have been handled (visited).
     */
    @NonNull final Set<Block> blocksHandled;

    protected SnakyBlockChain(@NonNull final P plugin, final @NonNull Block initialBlock,
                              @NonNull final Queue<Block> blocks, final @NonNull Queue<Block> nextBlocks,
                              @NonNull final Set<Block> blocksHandled) {
        this.plugin = plugin;
        this.world = initialBlock.getWorld();
        this.initialBlock = initialBlock;
        this.blocks = blocks;
        this.nextBlocks = nextBlocks;
        this.blocksHandled = blocksHandled;

        blocks.add(initialBlock);
    }

    protected SnakyBlockChain(@NonNull final P plugin, @NonNull final Block initialBlock) {
        this(plugin, initialBlock, new ArrayDeque<>(), new ArrayDeque<>(), new HashSet<>());
    }

    public static <P extends Plugin> SnakyBlockChain<P> create(@NonNull final P plugin,
                                                               @NonNull final Block initialBlock,
                                                               @NonNull final BlockHandler blockHandler) {
        return new SnakyBlockChain<P>(plugin, initialBlock) {
            @Override
            protected Collection<Block> handle(final Block block) {
                return blockHandler.handle(block);
            }
        };
    }

    @Override
    public boolean hasNext() {
        return !blocks.isEmpty() || !nextBlocks.isEmpty();
    }

    @Override
    public Block next() {
        // handle case when there are no blocks in at current layer
        if (blocks.isEmpty()) {
            // throw exception if there are no blocks on the next layer too (hasNext() might have not been checked)
            if (nextBlocks.isEmpty()) throw new NoSuchElementException("No more blocks available in BlockChain");

            // swap blocks and next blocks (micro-optimizations <3)
            // this switches the current layer to the one of blocks from handle(Block) of each previous layer's block
            val nextBlocks = this.nextBlocks;
            this.nextBlocks = blocks;
            blocks = nextBlocks;
        }

        // take tke next block queued and handle it
        val block = blocks.remove();
        // clear duplicate occurrences of this block in blocks and nextBlocks
        // TODO optimize
        while (true) {
            if (blocks.remove(block)) continue;
            break;
        }
        while (true) {
            if (nextBlocks.remove(block)) continue;
            break;
        }

        // get all blocks returned by this block's handling;
        // remove the block from nextBlocks too if it is present there
        val blocksToHandle = handle(block);
        // mark this block as handled
        blocksHandled.add(block);
        // mark all of the blocks returned as next to visit if they haven't been handled yet
        for (val blockToHandle : blocksToHandle) if (!blocksHandled.contains(blockToHandle)) nextBlocks
                .add(blockToHandle);

        return block;
    }

    /**
     * Gets the bukkit plugin contained by this object.
     *
     * @return bukkit plugin of this object
     */
    @Override
    public P getBukkitPlugin() {
        return plugin;
    }

    /**
     * Handles the specified block.
     *
     * @param block block to handle
     * @return next blocks to handle in this chain
     */
    protected abstract Collection<Block> handle(Block block);
}
