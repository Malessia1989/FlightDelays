package it.polito.tdp.extflightdelays.model;

public class Rotta {
	//classe creata per salvare i dati del DB
	private Airport source;
	private Airport destination;
	private double mediaDistanza;
	
	public Rotta(Airport source, Airport destination, double mediaDistanza) {
		super();
		this.source = source;
		this.destination = destination;
		this.mediaDistanza = mediaDistanza;
	}
	public Airport getSource() {
		return source;
	}
	public void setSource(Airport source) {
		this.source = source;
	}
	public Airport getDestination() {
		return destination;
	}
	public void setDestination(Airport destination) {
		this.destination = destination;
	}
	public double getMediaDistanza() {
		return mediaDistanza;
	}
	public void setMediaDistanza(double mediaDistanza) {
		this.mediaDistanza = mediaDistanza;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rotta other = (Rotta) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}

	
	

}
