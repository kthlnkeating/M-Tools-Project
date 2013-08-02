package com.pwc.us.rgi.m.tool.entry.fanin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pwc.us.rgi.m.parsetree.data.DataStore;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.data.FaninList;
import com.pwc.us.rgi.m.parsetree.data.Fanout;
import com.pwc.us.rgi.m.parsetree.data.FanoutBlocks;
import com.pwc.us.rgi.m.tool.entry.Block;
import com.pwc.us.rgi.m.tool.entry.BlocksSupply;
import com.pwc.us.rgi.struct.Filter;
import com.pwc.us.rgi.struct.Indexed;

public class EntryFaninsAggregator {
	private Block<Fanout, FaninMark> block;
	private BlocksSupply<Fanout, FaninMark> supply;
	private boolean filterInternalBlocks;
	
	public EntryFaninsAggregator(Block<Fanout, FaninMark> block, BlocksSupply<Fanout, FaninMark> supply, boolean filterInternalBlocks) {
		this.block = block;
		this.supply = supply;
		this.filterInternalBlocks = filterInternalBlocks;
	}
	
	private int updateFaninData(PathPieceToEntry data, Block<Fanout, FaninMark> b, FanoutBlocks<Fanout, FaninMark> fanoutBlocks, Map<Integer, PathPieceToEntry> datas) {
		int numChange = 0;
		FaninList<Fanout, FaninMark> faninList = fanoutBlocks.getFaninList(b);
		List<Indexed<Block<Fanout, FaninMark>>> faninBlocks = faninList.getFanins();
		for (Indexed<Block<Fanout, FaninMark>> ib : faninBlocks) {
			Block<Fanout, FaninMark> faninBlock = ib.getObject();
			int faninId = System.identityHashCode(faninBlock);
			PathPieceToEntry faninData = datas.get(faninId);
			boolean alreadyVisited = false; //faninData.exist();
			boolean useInternal = this.filterInternalBlocks && b.isInternal();
			int localChange = faninBlock.getData().update(faninData, data, useInternal);
			if ((localChange > 0) && (useInternal || ! alreadyVisited)) {
				this.updateFaninData(faninData, faninBlock, fanoutBlocks, datas);
			}
		}		
		return numChange;
	}
	
	private PathPieceToEntry get(FanoutBlocks<Fanout, FaninMark> fanoutBlocks, DataStore<PathPieceToEntry> store) {			
		Map<Integer, PathPieceToEntry> datas = new HashMap<Integer, PathPieceToEntry>();

		List<Block<Fanout, FaninMark>> blocks = fanoutBlocks.getBlocks();
		for (Block<Fanout, FaninMark> b : blocks) {
			int id = System.identityHashCode(b);
			FaninMark bd = b.getData();
			PathPieceToEntry data = bd.getLocalCopy();
			datas.put(id, data);
		}
		
		List<Block<Fanout, FaninMark>> evaluatedBlocks = fanoutBlocks.getEvaludatedBlocks();
		for (Block<Fanout, FaninMark> b : evaluatedBlocks) {
			PathPieceToEntry data = store.get(b);
			if (data.exist() || b.getData().isFanin()) {
				this.updateFaninData(data, b, fanoutBlocks, datas);
			}
		}
		
		for (int i=blocks.size()-1; i>=0; --i) {
			Block<Fanout, FaninMark> b = blocks.get(i);
			int id = System.identityHashCode(b);
			PathPieceToEntry data = datas.get(id);
			if ((! data.exist()) && b.getData().isFanin()) {
				b.getData().initialize(data);
				this.updateFaninData(data, b, fanoutBlocks, datas);
			}
		}
					
		for (Block<Fanout, FaninMark> bi : blocks) {
			if (! (bi.isInternal() && this.filterInternalBlocks)) {
				store.put(bi, datas);
			}
		}
		Block<Fanout, FaninMark> b = blocks.get(0);
		return store.put(b, datas);
	}
		
	public PathPieceToEntry get(DataStore<PathPieceToEntry> store, Filter<Fanout> filter) {
		PathPieceToEntry result = store.get(this.block);
		if (result == null) {
			Set<EntryId> missing = new HashSet<EntryId>();
			FanoutBlocks<Fanout, FaninMark> fanoutBlocks = this.block.getFanoutBlocks(this.supply, store, filter, missing);
			result = this.get(fanoutBlocks, store);
		}
		return result;
	}
}
