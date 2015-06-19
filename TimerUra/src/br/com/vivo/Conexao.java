package br.com.vivo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Conexao {

	static boolean isConnected = false;

	// EXTRANET
	/*
	 * private static String servidor = "10.72.18.129"; private static String
	 * porta = "1522"; private static String usuario = "10.72.18.129"; private
	 * static String senha = "10.72.18.129"; private static String nomeDeServico
	 * = "EQUIPEGO";
	 */

	// AVAYAPR1
	private static String servidor = "10.128.192.123";
	private static String porta = "1521";
	private static String usuario = "avaya";
	private static String senha = "avaya";
	private static String nomeDeServico = "AVAYAPR1";
	private static Connection con = null;

	public static Connection getConnection() throws SQLException {

		try {
			Arquivo.log("Tentando conectar com o banco de dados ... [Aguarde]");
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			con = DriverManager.getConnection("jdbc:oracle:thin:@//" + servidor + ":" + porta + "/" + nomeDeServico, usuario, senha);
			isConnected = true;
			Arquivo.log("Conectou");
		} catch (SQLException e) {
			Arquivo.log(e.getMessage());
			isConnected = false;
		} catch (ClassNotFoundException e) {
			Arquivo.log(e.getMessage());
			isConnected = false;
		} catch (InstantiationException e) {
			Arquivo.log(e.getMessage());
			isConnected = false;
		} catch (IllegalAccessException e) {
			Arquivo.log(e.getMessage());
			isConnected = false;
		}
		return con;
	}

	public static void closeConnnection(Connection con) {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void closeConnnection() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ResultSet consultar(String horarioInicial, String horarioFinal) throws SQLException {

		Connection con = null;
		ResultSet resultSet = null;
		try {
			con = getConnection();
			if (isConnected) {
				Arquivo.log("Formatando SQL");

				StringBuilder sql = new StringBuilder();

				sql.append("SELECT ");
				sql.append("  CHM_TIME ");
				sql.append(", CHM_ANI ");
				sql.append(", CHM_NUMINFOR ");
				sql.append(", CHM_CAMPO1 as RAMAL ");
				sql.append(", CHM_CAMPO2 as LOGINDAC ");
				sql.append(", CHM_CAMPO3 as NOTA ");
				sql.append(", CHM_CAMPO4 as TIPO_CLIENTE ");
				sql.append(", CHM_CAMPO5 as SEGMENTO_CLIENTE ");
				sql.append(", CHM_CAMPO6 as REGIONAL  ");
				sql.append(", CHM_CAMPO7 as SOLICITACAO_ATENDIDA ");
				sql.append(", CHM_CAMPO8 as NOTA_CONFIRMADA ");
				sql.append(", CHM_CAMPO9 as HUNT_EXTENSION ");
				sql.append(", CHM_CAMPO10 as OUTBOUND ");
				sql.append(", CHM_CAMPO11 as PROTOCOLO_ATENDIMENTO ");
				sql.append(", CHM_CAMPO12 as STATUS_ABORDAGEM ");
				sql.append(", CHM_CAMPO13 as STATUS_DERIVACAO ");
				sql.append(", CHM_IDSITE ");
				sql.append("FROM TB_CHAMADA_CHM_EX  ");

				sql.append("WHERE CHM_TIME >= to_date('");
				sql.append(horarioInicial);
				sql.append("','dd/mm/yyyy hh24:mi:ss') ");
				sql.append("AND CHM_TIME   <  to_date('");
				sql.append(horarioFinal);
				sql.append("','dd/mm/yyyy hh24:mi:ss') ");

				PreparedStatement prepared = con.prepareStatement(sql.toString());
				Arquivo.log("SQL Preparada :: " + sql.toString());
				Arquivo.log("Início da execução");
				resultSet = prepared.executeQuery();
				Arquivo.log("Fim da execução da Query");
			}

		} catch (SQLException e) {
			Arquivo.log(e.getMessage());
		} catch (Exception e) {
			Arquivo.log(e.getMessage());
		}
		return resultSet;
	}

	public static ResultSet consultarCount(String horaInicio, String horaFim) {
		Connection con = null;
		ResultSet resultSet = null;
		try {
			con = getConnection();
			if (isConnected) {
				Arquivo.log("Formatando SQL COUNT ");

				StringBuilder sql = new StringBuilder();

				sql.append("SELECT ");
				sql.append("  COUNT(CHM_TIME) as TOTAL ");
				sql.append("FROM TB_CHAMADA_CHM_EX  ");

				sql.append("WHERE CHM_TIME >= to_date('");
				sql.append(horaInicio);
				sql.append("','dd/mm/yyyy hh24:mi:ss') ");
				sql.append("AND CHM_TIME   <  to_date('");
				sql.append(horaFim);
				sql.append("','dd/mm/yyyy hh24:mi:ss') ");

				PreparedStatement prepared = con.prepareStatement(sql.toString());
				Arquivo.log("SQL COUNT Preparada :: " + sql.toString());
				Arquivo.log("Início da execução");
				resultSet = prepared.executeQuery();
				Arquivo.log("Fim da execução da Query");
			}

		} catch (SQLException e) {
			Arquivo.log(e.getMessage());
		} catch (Exception e) {
			Arquivo.log(e.getMessage());
		}
		return resultSet;
	}

}
