package it.polito.tdp.PremierLeague.model;

public class Event implements Comparable<Event> {
	public enum EventType{
		GOAL, 
		ESPULSIONE,
		INFORTUNIO
	}
	
	private int numeroAzione;
	private EventType type;
	private Player giocatore; //la variabile giocatore non serve anche perché il simulatore vuole solo il numero di gol in uscita, non sono richieste info sui giocatori
	public Event(int numeroAzione, EventType type) {
		super();
		this.numeroAzione = numeroAzione;
		this.type = type;
	//	this.giocatore = giocatore;
	}
	public int getNumeroAzione() {
		return numeroAzione;
	}
	public EventType getType() {
		return type;
	}
	public Player getGiocatore() {
		return giocatore;
	}
	@Override
	public int compareTo(Event o) {
		// TODO Auto-generated method stub
		return (int)(this.numeroAzione- o.numeroAzione);
	}
	
	
}
