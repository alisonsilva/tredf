package br.jus.tredf.oracle.monitoramento.model.filespace;

public class InfoRow {
	public String blocks;
	public String used;
	public String available;
	public String use;
	public String mounted;
	
	public InfoRow(String blocks, String used, String available, String use, String mounted) {
		this.blocks = blocks;
		this.used = used;
		this.available = available;
		this.use = use;
		this.mounted = mounted;
	}
	
	@Override
	public String toString() {
		String ret = "{blocks: " + blocks + ", "
				+"used: " + used + ", "
				+"available: " + available + ", "
				+"use: " + use + ", "
				+"mounted: " + mounted
				+ "}";
		return ret;
	}
}
