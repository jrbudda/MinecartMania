package com.afforess.minecartmania.farming;

import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.entity.MinecartManiaStorageCart;
import com.afforess.minecartmania.utils.StorageMinecartUtils;

public abstract class FarmingBase {
	
	protected static ArrayList<Block> findRoots(MinecartManiaStorageCart minecart, int blockId)
	{
		ArrayList<Block> allBlocks = StorageMinecartUtils.getBlocksInRange(minecart);
		
		ArrayList<Block> blocks = new ArrayList<Block>();
		for (Block block : allBlocks)
		{
			if(block.getTypeId() == blockId)
			{
				Block root = findRoot(block, blockId);
				if(root != null && !blocks.contains(root))
				{
					blocks.add(root);
				}
			}
		}
		
		return blocks;
	}
	
	protected static Block findRoot(Block block, int blockId)
	{
		if(block.getTypeId() == blockId) //block is a log, search downwards
		{
			return findRoot(block.getRelative(BlockFace.DOWN), blockId);
		}
		else if(block.getTypeId() == Item.DIRT.getId()) //block is dirt, that must be the root
		{
			return block;
		}
		
		//could not find root, unexpected block in the way
		return null;
	}
}
