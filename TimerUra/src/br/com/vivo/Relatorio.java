package br.com.vivo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Relatorio {
	
	public static Long countSQL = 0L;
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

	public static boolean gerarArquivoLocal() throws SQLException {
		boolean sucesso = true;
		if (Arquivo.conexaoDeRede) {
			if (!Arquivo.existe()) {
				String horaInicio = horaFormatada(calculaHora(-Task.horasAntes));
				String horaFim = horaFormatada(calculaHora(-(Task.horasAntes - Task.intervaloDeHoras)));
				calcularCountSQL(horaInicio, horaFim);
				Arquivo.logCount("\r\n" + Calendar.getInstance().getTime() + " - " + Arquivo.nomeArquivo);
				ResultSet rs = Conexao.consultar(horaInicio, horaFim);
				Arquivo.logCount(countSQL.toString());
				countLinesLocal = 0L;
				
				Writer writer;
		        writer = null;
		        
				if (Conexao.isConnected) {
					Arquivo.log("----------------------------------------------------------------------");
					Arquivo.log("Início do preenchimento do arquivo local. [Aguarde ...]");
					Arquivo.nomeArquivoTmp = Arquivo.nomeArquivo;
					try {
						//FileWriter writer = new FileWriter(Arquivo.pastaLocal+Arquivo.nomeArquivo);
						writer = new FileWriter(Arquivo.pastaLocal+Arquivo.nomeArquivo);
//						writer = new FileWriter(Arquivo.pastaLocal+Arquivo.nomeArquivo);
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
								/*if (countLinesLocal>=5000L){
									break;
								}*/
							}
						}
						writer.close();
						Arquivo.log("Arquivo local temporário foi criado");
						Arquivo.logCount(countLinesLocal.toString());
					} catch (IOException e) {
						sucesso = false;
						Arquivo.log("--> " + e.getMessage());
						if(writer != null){
							try {
								writer.flush();
								writer.close();
							} catch (IOException e1) {
								sucesso = false;
								e1.printStackTrace();
								Arquivo.log(e1.getMessage());
							}
							
						}
					}
					catch (Exception e) {
						sucesso = false;
						Arquivo.log("--> " + e.getMessage());
						if(writer != null){
							try {
								writer.flush();
								writer.close();
							} catch (IOException e1) {
								sucesso = false;
								e1.printStackTrace();
								Arquivo.log(e1.getMessage());
							}
							
						}
					}finally {
						Conexao.closeConnnection();
//						writer.flush();
//						writer.close();
					}
				} else {
					sucesso = false;
					Arquivo.log("Houve um problema na conexão com o banco de dados.");
				}
			}
			else{
				sucesso = false;
				Arquivo.log("Arquivo já existe na rede.");
			}
		} else {
			sucesso = false;
			Arquivo.log("Houve um problema na conexão de rede.");
		}
		return sucesso;
	}
	
	/*public static void gerarArquivoAusente(int horaInicial) throws SQLException {
		if (Arquivo.conexaoDeRede) {
			//if (!Arquivo.existe()) {
				String horaInicio = horaFormatada(calculaHoraPassada(horaInicial));
				String horaFim = horaFormatada(calculaHoraPassada( horaInicial + Task.intervaloDeHoras));
				calcularCountSQL(horaInicio, horaFim);
				Arquivo.logCount(Calendar.getInstance().getTime() + " - " + Arquivo.nomeArquivo);
				ResultSet rs = Conexao.consultar(horaInicio, horaFim);
				Arquivo.logCount(countSQL.toString());
				countLinesLocal = 0L;
				if (Conexao.isConnected) {
					Arquivo.log("----------------------------------------------------------------------");
					Arquivo.log("Início do preenchimento do arquivo local. [Aguarde ...]");
					try {
						FileWriter writer = new FileWriter(Arquivo.pastaLocal+Arquivo.nomeArquivo);
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
						Arquivo.logCount(countLinesLocal.toString()+"\r\n");
					} catch (IOException e) {
						Arquivo.log(e.getMessage());
					} finally {
						Conexao.closeConnnection();
					}
				} else {
					Arquivo.log("Houve um problema na conexão com o banco de dados.");
				}
			//}
		} else {
			Arquivo.log("Houve um problema na conexão de rede.");
		}
	}*/
	
	public static boolean gerarArquivoAusente(String nomeArquivo) throws SQLException {
		boolean sucesso = true;
		if (Arquivo.conexaoDeRede) {
			//if (!Arquivo.existe()) {
				String horaInicio = horaFormatada(stringToCalendar(nomeArquivo));
				Calendar horaFimCalendar = Calendar.getInstance();
				int nrHoraFim = stringToCalendar(nomeArquivo).get(Calendar.HOUR_OF_DAY) + Task.intervaloDeHoras;
				horaFimCalendar.set(Calendar.DAY_OF_MONTH,stringToCalendar(nomeArquivo).get(Calendar.DAY_OF_MONTH));
				horaFimCalendar.set(Calendar.HOUR_OF_DAY,nrHoraFim);
				horaFimCalendar.set(Calendar.MONTH,stringToCalendar(nomeArquivo).get(Calendar.MONTH));
				horaFimCalendar.set(Calendar.SECOND,stringToCalendar(nomeArquivo).get(Calendar.SECOND));
				horaFimCalendar.set(Calendar.MINUTE,stringToCalendar(nomeArquivo).get(Calendar.MINUTE));
				String horaFim = horaFormatada(horaFimCalendar);
				
				
				calcularCountSQL(horaInicio, horaFim);
				Arquivo.logCount("\r\n" + Calendar.getInstance().getTime() + " - " + Arquivo.nomeArquivo);
				ResultSet rs = Conexao.consultar(horaInicio, horaFim);
				Arquivo.logCount(countSQL.toString());
				countLinesLocal = 0L;
				
				Writer writer;
		        writer = null;
		        
				if (Conexao.isConnected) {
					Arquivo.log("----------------------------------------------------------------------");
					Arquivo.log("Início do preenchimento do arquivo local. [Aguarde ...]");
					try {
						writer = new FileWriter(Arquivo.pastaLocal+Arquivo.nomeArquivo);
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
						Arquivo.logCount(countLinesLocal.toString()+"\r\n");
					} catch (IOException e) {
						sucesso = false;
						Arquivo.log("--> " + e.getMessage());
						if(writer != null){
							try {
								writer.flush();
								writer.close();
							} catch (IOException e1) {
								sucesso = false;
								e1.printStackTrace();
								Arquivo.log(e1.getMessage());
							}
							
						}
					}
					catch (Exception e) {
						sucesso = false;
						Arquivo.log("--> " + e.getMessage());
						if(writer != null){
							try {
								writer.flush();
								writer.close();
							} catch (IOException e1) {
								sucesso = false;
								e1.printStackTrace();
								Arquivo.log(e1.getMessage());
							}
							
						}
					} finally {
						Conexao.closeConnnection();
					}
				} else {
					sucesso = false;
					Arquivo.log("Houve um problema na conexão com o banco de dados.");
				}
			//}
		} else {
			sucesso = false;
			Arquivo.log("Houve um problema na conexão de rede.");
		}
		return sucesso;
	}
	
	
	
	public static boolean gerarArquivoAusente(Calendar arquivoAusente) throws SQLException {
		boolean sucesso = true;
		if (Arquivo.conexaoDeRede) {
			//if (!Arquivo.existe()) {
				String horaInicio = horaFormatada(arquivoAusente);
				Calendar horaFimCalendar = Calendar.getInstance();
				int nrHoraFim = arquivoAusente.get(Calendar.HOUR_OF_DAY) + Task.intervaloDeHoras;
				
				horaFimCalendar.set(Calendar.DAY_OF_MONTH,arquivoAusente.get(Calendar.DAY_OF_MONTH));
				horaFimCalendar.set(Calendar.HOUR_OF_DAY,nrHoraFim);
				horaFimCalendar.set(Calendar.MONTH,arquivoAusente.get(Calendar.MONTH));
				horaFimCalendar.set(Calendar.SECOND,arquivoAusente.get(Calendar.SECOND));
				horaFimCalendar.set(Calendar.MINUTE,arquivoAusente.get(Calendar.MINUTE));
				
				String horaFim = horaFormatada(horaFimCalendar);
				calcularCountSQL(horaInicio, horaFim);
				Arquivo.logCount("\r\n" + Calendar.getInstance().getTime() + " - " + Arquivo.nomeArquivo);
				ResultSet rs = Conexao.consultar(horaInicio, horaFim);
				Arquivo.logCount(countSQL.toString());
				countLinesLocal = 0L;
				
				Writer writer;
		        writer = null;
		        
				if (Conexao.isConnected) {
					Arquivo.log("----------------------------------------------------------------------");
					Arquivo.log("Início do preenchimento do arquivo local. [Aguarde ...]");
					try {
						//FileWriter writer = new FileWriter(Arquivo.pastaLocal+Arquivo.nomeArquivo);
						writer = new FileWriter(Arquivo.pastaLocal+Arquivo.nomeArquivo);
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
						Arquivo.logCount(countLinesLocal.toString());
					} catch (IOException e) {
						//Arquivo.log(e.getMessage());
						sucesso = false;
						Arquivo.log("--> " + e.getMessage());
						if(writer != null){
							try {
								writer.flush();
								writer.close();
							} catch (IOException e1) {
								sucesso = false;
								e1.printStackTrace();
								Arquivo.log(e1.getMessage());
							}
							
						}
					} 
					catch (Exception e) {
						sucesso = false;
						Arquivo.log("--> " + e.getMessage());
						if(writer != null){
							try {
								writer.flush();
								writer.close();
							} catch (IOException e1) {
								sucesso = false;
								e1.printStackTrace();
								Arquivo.log(e1.getMessage());
							}
							
						}
					}
					finally {
						Conexao.closeConnnection();
					}
				} else {
					sucesso = false;
					Arquivo.log("Houve um problema na conexão com o banco de dados.");
				}
			//}
		} else {
			sucesso = false;
			Arquivo.log("Houve um problema na conexão de rede.");
		}
		return sucesso;
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
	
	
	public static Calendar calculaHoraPassada(int hora) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY,hora);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c;
	}
	

	private static String horaFormatada(Calendar c) {
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		DateFormat dtHora = DateFormat.getDateTimeInstance();
		return dtHora.format(c.getTime());
	}
	
	/*public static String horaFormatada(String s) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH,Integer.parseInt(s.substring(0, 1)));
		c.set(Calendar.HOUR_OF_DAY,Integer.parseInt(s.substring(2, 3)));
		c.set(Calendar.MONTH,Integer.parseInt(s.substring(4, 5)));
		c.set(Calendar.SECOND,0);
		c.set(Calendar.MINUTE,0);
		DateFormat dtHora = DateFormat.getDateTimeInstance();
		return dtHora.format(c.getTime());
	}*/
	
	public static Calendar stringToCalendar(String s) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH,Integer.parseInt(s.substring(0, 2)));
		c.set(Calendar.MONTH,(Integer.parseInt(s.substring(2, 4))) - 1   );
		c.set(Calendar.HOUR_OF_DAY,Integer.parseInt(s.substring(4, 6)));
		c.set(Calendar.SECOND,0);
		c.set(Calendar.MINUTE,0);
		//DateFormat dtHora = DateFormat.getDateTimeInstance();
		return c;
	}
	
}
