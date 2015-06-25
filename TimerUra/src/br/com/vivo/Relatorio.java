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
	
	public static Long countSQL;
	public static Long countLinesLocal;
	

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
			if (!Arquivo.existe()) {
				String horaInicio = horaFormatada(calculaHora(-Task.horasAntes));
				String horaFim = horaFormatada(calculaHora(-(Task.horasAntes - Task.intervaloDeHoras)));
				calcularCountSQL(horaInicio, horaFim);
				Arquivo.logCount(Calendar.getInstance().getTime() + " - " + Arquivo.nomeArquivo);
				ResultSet rs = Conexao.consultar(horaInicio, horaFim);
				Arquivo.logCount(countSQL.toString());
				countLinesLocal = 0L;
				if (Conexao.isConnected) {
					Arquivo.log("-------------------------------------------------------");
					Arquivo.log("Início do preenchimento do arquivo local. [Aguarde ...]");
					try {
						FileWriter writer = new FileWriter(Arquivo.nomeArquivo);
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
								countLinesLocal++;
							}
						}
						writer.close();
						Arquivo.log("Arquivo local temporário foi criado");
						Arquivo.log("-------------------------------------------------------");
						Arquivo.logCount(countLinesLocal.toString());
					} catch (IOException e) {
						Arquivo.log(e.getMessage());
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
	
	public static void gerarArquivoAusente(int horaInicial) throws SQLException {
		if (Arquivo.conexaoDeRede) {
			if (!Arquivo.existe()) {
				String horaInicio = horaFormatada(calculaHoraPassada(horaInicial));
				String horaFim = horaFormatada(calculaHoraPassada( horaInicial + Task.intervaloDeHoras));
				calcularCountSQL(horaInicio, horaFim);
				Arquivo.logCount(Calendar.getInstance().getTime() + " - " + Arquivo.nomeArquivo);
				ResultSet rs = Conexao.consultar(horaInicio, horaFim);
				Arquivo.logCount(countSQL.toString());
				countLinesLocal = 0L;
				if (Conexao.isConnected) {
					Arquivo.log("-------------------------------------------------------");
					Arquivo.log("Início do preenchimento do arquivo local. [Aguarde ...]");
					try {
						FileWriter writer = new FileWriter(Arquivo.nomeArquivo);
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
								countLinesLocal++;
							}
						}
						writer.close();
						Arquivo.log("Arquivo local temporário foi criado");
						//Arquivo.log("-------------------------------------------------------");
						Arquivo.logCount(countLinesLocal.toString());
					} catch (IOException e) {
						Arquivo.log(e.getMessage());
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

	private static void calcularCountSQL(String horaInicio, String horaFim) throws SQLException {
		ResultSet rsCount = Conexao.consultarCount(horaInicio, horaFim);
		Long  total = 0L;
		if (rsCount != null) {
			while (rsCount.next()) {
				total = rsCount.getLong(1);
			}
			countSQL = total;
		}
	}

	private static Calendar calculaHora(int quantidadeDeHorasPassadas) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, quantidadeDeHorasPassadas);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c;
	}
	
	
	private static Calendar calculaHoraPassada(int hora) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY,hora);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c;
	}
	

	private static String horaFormatada(Calendar c) {
		DateFormat dtHora = DateFormat.getDateTimeInstance();
		return dtHora.format(c.getTime());
	}
}
