package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.PremierLeague.model.Event.EventType;

public class Simulatore {
	//DATI IN INGRESS
	List<Player> giocatori;
	List<Player> giocatori1;
	List<Player> giocatori2;
	
	Graph<Player, DefaultWeightedEdge> grafo;
	Match partita;
	int N; //AZIONI DA SIMULARE
	Team squadra1;
	Team squadra2;
	
	
	//DATI IN USCITA
	int golSquadra1;
	int golSquadra2;
	List<Player> espulsi;
	
	//STATO DEL MONDO
	int giocatoriSquadra1;
	int giocatoriSquadra2;
	
	//CODA DEGLI EVENTI
	PriorityQueue<Event> queue;

	public Simulatore(Graph<Player, DefaultWeightedEdge> grafo, Match partita, int n, Team squadra1, Team squadra2) {
		super();
		this.grafo = grafo;
		this.partita = partita;
		N = n;
		this.squadra1 = squadra1;
		this.squadra2 = squadra2;
	}
	
	public void init() {
		
		this.golSquadra1 = 0;
		this.golSquadra2 = 0;
		this.espulsi = new ArrayList<>();
		this.queue = new PriorityQueue<>();
		
		this.giocatori = new ArrayList<>(this.grafo.vertexSet());
		
		this.giocatoriSquadra1 = 11;
		this.giocatoriSquadra2 = 11;
		
		for(int i = 0; i<this.N; i++) {
			generaEvento(i);
		}
		
		for(Player p : this.giocatori) {
			if(p.getTeamID() == this.squadra1.getTeamID())
				this.giocatori1.add(p);
			else
				this.giocatori2.add(p);
		}
		
	}
	
	public void run() {
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			processEvent(e);
		}
	}
	public void processEvent(Event e) {
		switch(e.getType()) {
		
		case GOAL:
			
			if(this.giocatoriSquadra1 > this.giocatoriSquadra2) {
				this.golSquadra1 ++;
			}else if(this.giocatoriSquadra2 > this.giocatoriSquadra1) {
				this.golSquadra2++;
			}else {
				//SEGNA LA SQUADRA A CUI APPARTIENE IL GIOCATORE MIGLIORE
				Player migliore = calcolaGiocatoreMigliore();
				if(migliore.getTeamID() == this.squadra1.getTeamID()) {
					this.golSquadra1++;
				}else
					this.golSquadra2++;
			}
			
			
			break;
		case ESPULSIONE:
			
			double p = Math.random();
			Player migliore = calcolaGiocatoreMigliore();
			if(p > 0.4) {
				if(this.squadra1.getTeamID() == migliore.getTeamID()) {
					
					this.giocatoriSquadra1--;
					
					int indice = (int) (Math.random()*this.giocatori1.size());
					Player eliminato = giocatori1.get(indice);
					giocatori1.remove(eliminato);
					giocatori.remove(eliminato);
					
				}
				else {
					
					this.giocatoriSquadra2--;
					
					int indice = (int) (Math.random()*this.giocatori2.size());
					Player eliminato = giocatori1.get(indice);
					giocatori2.remove(eliminato);
					giocatori.remove(eliminato);
				}
			}else {
				if(this.squadra1.getTeamID() != migliore.getTeamID()) {
					
					this.giocatoriSquadra1--;
					
					int indice = (int) (Math.random()*this.giocatori1.size());
					Player eliminato = giocatori1.get(indice);
					giocatori1.remove(eliminato);
					giocatori.remove(eliminato);
				}
				else {
					
					this.giocatoriSquadra2--;
					
					int indice = (int) (Math.random()*this.giocatori2.size());
					Player eliminato = giocatori1.get(indice);
					giocatori2.remove(eliminato);
					giocatori.remove(eliminato);
				}
			}
			
			break;
		case INFORTUNIO: 
			
			double aumento = Math.random();
			
			if(aumento > 0.5) {
				for(int i=1; i<=2; i++) {
					generaEvento(this.N+1);
					this.N++;
				}
			}
				
			else
			 {
				for(int i=1; i<=3; i++) {
					generaEvento(this.N+1);
					this.N++;
				}
			}
			break;
		
		
		}
	}
	public void generaEvento(int i) {
		double prob = Math.random();
		
		if(prob < 0.5) {
			//GOAL
			Event e = new Event(i, EventType.GOAL);
			this.queue.add(e);
		}else if( prob < 0.8) {
			//ESPULSIONE
			Event e = new Event(i, EventType.ESPULSIONE);
			this.queue.add(e);
		}else {
			//INFORTUNIO
			Event e = new Event(i, EventType.INFORTUNIO);
			this.queue.add(e);
		}
	}
	public Player calcolaGiocatoreMigliore() {
		Player migliore = null;
		double deltaBest = 0;
		
		for(Player p : this.giocatori) {
			
			double grado = calcolaGrado(p);
			if(grado > deltaBest) {
				deltaBest = grado;
				migliore = p;
			}
		}
		

		return migliore;
	}
	
	public double calcolaGrado(Player p) {
		double uscenti = 0;
		
		for(DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(p)) {
			uscenti += this.grafo.getEdgeWeight(e);
		}
		//PESO ARCHI ENTRANTI
		double entranti = 0;
		for(DefaultWeightedEdge e : this.grafo.incomingEdgesOf(p)) {
			entranti += this.grafo.getEdgeWeight(e);
		}
	
		
		double grado = uscenti - entranti;
		
		return grado;
	}
}
