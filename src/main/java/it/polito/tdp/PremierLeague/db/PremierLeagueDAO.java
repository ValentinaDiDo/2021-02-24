package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Player;
import it.polito.tdp.PremierLeague.model.Team;

public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Team> listAllTeams(){
		String sql = "SELECT * FROM Teams";
		List<Team> result = new ArrayList<Team>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Team team = new Team(res.getInt("TeamID"), res.getString("Name"));
				result.add(team);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(int matchId){
		String sql = "SELECT * FROM Actions WHERE MatchID = ? ";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, matchId);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Match> listAllMatches(){
		String sql = "SELECT m.MatchID, m.TeamHomeID, m.TeamAwayID, m.teamHomeFormation, m.teamAwayFormation, m.resultOfTeamHome, m.date, t1.Name, t2.Name   "
				+ "FROM Matches m, Teams t1, Teams t2 "
				+ "WHERE m.TeamHomeID = t1.TeamID AND m.TeamAwayID = t2.TeamID";
		List<Match> result = new ArrayList<Match>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				
				Match match = new Match(res.getInt("m.MatchID"), res.getInt("m.TeamHomeID"), res.getInt("m.TeamAwayID"), res.getInt("m.teamHomeFormation"), 
							res.getInt("m.teamAwayFormation"),res.getInt("m.resultOfTeamHome"), res.getTimestamp("m.date").toLocalDateTime(), res.getString("t1.Name"),res.getString("t2.Name"));
				
				
				result.add(match);

			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	public List<Player> getPlayerGrafo(int matchID){
		String sql = "SELECT p.*, a.TeamID as t "
				+ "FROM Players p, Matches m, Actions a "
				+ "WHERE p.PlayerID = a.PlayerID AND a.MatchID = m.MatchID AND m.MatchID = ?";
		
		List<Player> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, matchID);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				int teamId = res.getInt("t");
				player.setTeamID(teamId);
				result.add(player);
				
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public List<Adiacenza> getAdiacenze(int matchID, Map<Integer, Player> map){
		String sql = "SELECT a1.PlayerID as id1, a2.PlayerID as id2, ((a1.TotalSuccessfulPassesAll + a1.Assists)/a1.TimePlayed - (a2.TotalSuccessfulPassesAll + a2.Assists)/a2.TimePlayed) as delta "
				+ "FROM Actions a1, Actions a2 "
				+ "WHERE a1.PlayerID > a2.PlayerID AND a1.MatchID = a2.MatchID AND a1.MatchID = ? AND a1.TeamID <> a2.TeamID";

	
		List<Adiacenza> result = new ArrayList<>();
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, matchID);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				int id1 = res.getInt("id1");
				int id2 = res.getInt("id2");
				if(map.containsKey(id1) && map.containsKey(id2)) {
					Player p1 = map.get(id1);
					Player p2 = map.get(id2);
					double delta = res.getDouble("delta");
					Adiacenza a = new Adiacenza(p1,p2,delta);
					result.add(a);
				}
						
				
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	
	}
	
	public List<Team> getTeamsPartita(int matchID, Map<Integer, Team> map){
		String sql = "SELECT m.TeamHomeID as i1, m.TeamAwayID as i2 "
				+ "FROM  Matches m "
				+ "WHERE m.MatchID = ?";
		List<Team> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, matchID);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				int id1 = res.getInt("i1");
				int id2 = res.getInt("i2");
				if(map.containsKey(id1) && map.containsKey(id2)) {
					Team t1 = map.get(id1);
					Team t2 = map.get(id2);
					
					result.add(t1);
					result.add(t2);
				}
				
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
