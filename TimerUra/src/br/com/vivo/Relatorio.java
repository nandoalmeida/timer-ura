package br.com.vivo;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Relatorio {

	@Deprecated
	public static void gerarArquivo() throws SQLException {
		if (!Arquivo.existe()) {
			String horaInicio = horaFormatada(calculaHora(-4));
			String horaFim = horaFormatada(calculaHora(-1));
			ResultSet rs = Conexao.consultar(horaInicio, horaFim);
			Arquivo.escreve("CHM_TIME,");
			Arquivo.escreve("CHM_ANI,");
			Arquivo.escreve("CHM_NUMINFOR,");
			Arquivo.escreve("RAMAL,");
			Arquivo.escreve("LOGINDAC,");
			Arquivo.escreve("NOTA,");
			Arquivo.escreve("TIPO_CLIENTE,");
			Arquivo.escreve("SEGMENTO_CLIENTE,");
			Arquivo.escreve("REGIONAL,");
			Arquivo.escreve("SOLICITACAO_ATENDIDA,");
			Arquivo.escreve("NOTA_CONFIRMADA,");
			Arquivo.escreve("HUNT_EXTENSION,");
			Arquivo.escreve("OUTBOUND,");
			Arquivo.escreve("PROTOCOLO_ATENDIMENTO,");
			Arquivo.escreve("STATUS_ABORDAGEM,");
			Arquivo.escreve("STATUS_DERIVACAO,");
			Arquivo.escreve("CHM_IDSITE");
			Arquivo.escreve("\r\n");
			if (rs != null) {
				while (rs.next()) {
					Arquivo.escreve(rs.getDate("CHM_TIME").toString() + ",");
					Arquivo.escreve(escreveValorColunaString(rs, "CHM_ANI", ","));
					Arquivo.escreve(escreveValorColunaString(rs, "CHM_NUMINFOR", ","));
					Arquivo.escreve(escreveValorColunaString(rs, "RAMAL", ","));
					Arquivo.escreve(escreveValorColunaString(rs, "LOGINDAC", ","));
					Arquivo.escreve(escreveValorColunaString(rs, "NOTA", ","));
					Arquivo.escreve(escreveValorColunaString(rs, "TIPO_CLIENTE", ","));
					Arquivo.escreve(escreveValorColunaString(rs, "SEGMENTO_CLIENTE", ","));
					Arquivo.escreve(escreveValorColunaString(rs, "REGIONAL", ","));
					Arquivo.escreve(escreveValorColunaString(rs, "SOLICITACAO_ATENDIDA", ","));
					Arquivo.escreve(escreveValorColunaString(rs, "NOTA_CONFIRMADA", ","));
					Arquivo.escreve(escreveValorColunaString(rs, "HUNT_EXTENSION", ","));
					Arquivo.escreve(escreveValorColunaString(rs, "OUTBOUND", ","));
					Arquivo.escreve(escreveValorColunaString(rs, "PROTOCOLO_ATENDIMENTO", ","));
					Arquivo.escreve(escreveValorColunaString(rs, "STATUS_ABORDAGEM", ","));
					Arquivo.escreve(escreveValorColunaString(rs, "STATUS_DERIVACAO", ","));
					Arquivo.escreve(escreveValorColunaString(rs, "CHM_IDSITE", ""));
					Arquivo.escreve("\r\n");
				}
			}
			Conexao.closeConnnection();
		}
	}

	private static String escreveValorColunaString(ResultSet rs, String coluna, String sufixo) {
		String valorColuna = "\"\"" + sufixo;
		try {
			if (rs.getString(coluna) != null) {
				if (!rs.getString(coluna).equals("null")) {
					valorColuna = "\"" + rs.getString(coluna) + "\"" + sufixo;
				}
			}
		} catch (SQLException e) {
			Arquivo.log(e.getMessage());
		}
		return valorColuna;
	}

	private static String escreveValorColunaDate(ResultSet rs, String coluna, String sufixo) {
		String valorColuna = "\"\"" + sufixo;
		Date date;
		try {
			date = rs.getDate(coluna);
			if (date != null) {
				valorColuna = new SimpleDateFormat("dd/MM/yy").format(new java.util.Date(date.getTime())) + sufixo;
			}
		} catch (SQLException e) {
			Arquivo.log(e.getMessage());
		}

		return valorColuna;
	}

	public static void gerarArquivoLocal() throws SQLException {
		if (Arquivo.conexaoDeRede) {
			//if (!Arquivo.existe()) {
			if (true) {
				/*
				String horaInicio = horaFormatada(calculaHora(-4));
				String horaFim = horaFormatada(calculaHora(-1));
				*/
				String horaInicio = "12/05/2015 21:00:00";
				String horaFim = "13/05/2015 00:00:00";
				ResultSet rs = Conexao.consultar(horaInicio, horaFim);
				
				
				ResultSet rsCount = Conexao.consultarCount(horaInicio, horaFim);
				Long  total = 0L;
				if (rsCount != null) {
					while (rsCount.next()) {
						total = rsCount.getLong(1);
					}
					System.out.println("COUNT >> "+ total);
				}
						
				
				
				
				if (Conexao.isConnected) {
					Arquivo.log("Início do preenchimento do arquivo local. [Aguarde ...]");
					try {
						FileWriter writer = new FileWriter(Arquivo.nomeArquivoFormatado());
						writer.append("CHM_TIME,");
						writer.append("\"CHM_ANI\",");
						writer.append("\"CHM_NUMINFOR\",");
						writer.append("\"RAMAL\",");
						writer.append("\"LOGINDAC\",");
						writer.append("\"NOTA\",");
						writer.append("\"TIPO_CLIENTE\",");
						writer.append("\"SEGMENTO_CLIENTE\",");
						writer.append("\"REGIONAL\",");
						writer.append("\"SOLICITACAO_ATENDIDA\",");
						writer.append("\"NOTA_CONFIRMADA\",");
						writer.append("\"HUNT_EXTENSION\",");
						writer.append("\"OUTBOUND\",");
						writer.append("\"PROTOCOLO_ATENDIMENTO\",");
						writer.append("\"STATUS_ABORDAGEM\",");
						writer.append("\"STATUS_DERIVACAO\",");
						writer.append("\"CHM_IDSITE\"");
						writer.append("\r\n");
						if (rs != null) {
							while (rs.next()) {
								writer.append(escreveValorColunaDate(rs, "CHM_TIME", ","));
								writer.append(escreveValorColunaString(rs, "CHM_ANI", ","));
								writer.append(escreveValorColunaString(rs, "CHM_NUMINFOR", ","));
								writer.append(escreveValorColunaString(rs, "RAMAL", ","));
								writer.append(escreveValorColunaString(rs, "LOGINDAC", ","));
								writer.append(escreveValorColunaString(rs, "NOTA", ","));
								writer.append(escreveValorColunaString(rs, "TIPO_CLIENTE", ","));
								writer.append(escreveValorColunaString(rs, "SEGMENTO_CLIENTE", ","));
								writer.append(escreveValorColunaString(rs, "REGIONAL", ","));
								writer.append(escreveValorColunaString(rs, "SOLICITACAO_ATENDIDA", ","));
								writer.append(escreveValorColunaString(rs, "NOTA_CONFIRMADA", ","));
								writer.append(escreveValorColunaString(rs, "HUNT_EXTENSION", ","));
								writer.append(escreveValorColunaString(rs, "OUTBOUND", ","));
								writer.append(escreveValorColunaString(rs, "PROTOCOLO_ATENDIMENTO", ","));
								writer.append(escreveValorColunaString(rs, "STATUS_ABORDAGEM", ","));
								writer.append(escreveValorColunaString(rs, "STATUS_DERIVACAO", ","));
								writer.append(escreveValorColunaString(rs, "CHM_IDSITE", ""));
								writer.append("\r\n");
								writer.flush();
							}
						}
						writer.close();
						Arquivo.log("Arquivo local temporário foi criado");
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						Conexao.closeConnnection();
					}
				} else {
					Arquivo.log("Houve um problema na conexão com o banco de dados.");

				}
			}
		} else {
			Arquivo.log("Houve um problema na conexão de rede.");
		}
	}

	private static Calendar calculaHora(int quantidadeDeHorasPassadas) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, quantidadeDeHorasPassadas);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c;
	}

	private static String horaFormatada(Calendar c) {
		DateFormat dtHora = DateFormat.getDateTimeInstance();
		return dtHora.format(c.getTime());
	}
}
