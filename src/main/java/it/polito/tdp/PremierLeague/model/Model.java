package it.polito.tdp.PremierLeague.model;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	PremierLeagueDAO dao;
	List<Match> partite;
	List<Player> giocatori;
	Map<Integer, Player> mGiocatori;
	Graph<Player, DefaultWeightedEdge> grafo;
	List<Action> actions;
	List<Adiacenza> adiacenze;
	Match partita;
	
	public Model () {
		this.dao = new PremierLeagueDAO();
	}
	public List<Match> getAllMatches(){
		return this.dao.listAllMatches();
	}
	public void creaGrafo(Match match) {
		this.partita = match;
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		this.giocatori = this.dao.getPlayerGrafo(match.getMatchID());
		this.mGiocatori = new TreeMap<>();
		
		for(Player p : giocatori) {
			this.mGiocatori.put(p.getPlayerID(), p);
		}
		
		Graphs.addAllVertices(this.grafo, giocatori);
		
		this.actions = this.dao.listAllActions(match.getMatchID());
		
		//CALCOLO EFFICIENZA GIOCATORI
		for(Action a : this.actions) {

			int passaggiRiusciti = a.getTotalSuccessfulPassesAll();
			int assists = a.getAssists();
			int minuti = a.getTimePlayed();
			
			double efficienza = (passaggiRiusciti + assists)/minuti;
			
			this.mGiocatori.get(a.getPlayerID()).setEfficienza(efficienza);
			this.mGiocatori.get(a.getPlayerID()).setTeamID(a.getTeamID());
		}
		
		//METODO CON QUEERY
		
		this.adiacenze = this.dao.getAdiacenze(match.getMatchID(), mGiocatori);
		//RIEMPI ARCHI
		for(Adiacenza a : adiacenze) {
			if(a.getDelta() >= 0) { //peso p1 > peso p2
				Graphs.addEdge(this.grafo, a.getP1(), a.getP2(), a.getDelta());
			}else
				Graphs.addEdge(this.grafo, a.getP2(), a.getP1(), -a.getDelta());
		}
		
		/*
		//METODO SENZA QUEERY - VA BENE PERCHE' POCHI ARCHI
		//AGGIUNGO GLI ARCHI
		for(Player p1 : giocatori) {
			for(Player p2 : giocatori) {
				if(!p1.equals(p2) && p1.getTeamID()!=p2.getTeamID()) {
					
					DefaultWeightedEdge e1 = this.grafo.getEdge(p1, p2);
					DefaultWeightedEdge e2 = this.grafo.getEdge(p2, p1);
					if(e1 == null && e2 == null) {
						
						//SONO AVVERSARI, POSSO COLLEGARLI
						double delta = Math.abs(p1.getEfficienza() - p2.getEfficienza());
						
						if(p1.getEfficienza() > p2.getEfficienza())
							Graphs.addEdge(this.grafo, p1, p2, delta);
						else
							Graphs.addEdge(this.grafo, p2, p1, delta);
					}
				}
			}
		}
		*/
	}
	public Graph<Player, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}
	
	public Player calcolaGiocatoreMigliore() {
		Player migliore = null;
		double deltaBest = 0;
		
		for(Player p : this.grafo.vertexSet()) {
			
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
	
	public void Simula(int N) {
		List<Team> lteam = this.dao.listAllTeams();
		Map<Integer, Team> mTeam = new TreeMap<>();
		for(Team t : lteam) {
			mTeam.put(t.teamID, t);
		}
		
		//List<Team> teamPartita = this.dao.getTeamsPartita(this.partita.getMatchID(), mTeam);
		
		//Team t1 = teamPartita.get(0);
		//Team t2 = teamPartita.get(1);
		
		Team t1 = mTeam.get(partita.getTeamHomeID());
		Team t2 = mTeam.get(this.partita.getTeamAwayID());
		Simulatore simulatore = new Simulatore(this.grafo, this.partita, N, t1, t2 );
		
	}
}
