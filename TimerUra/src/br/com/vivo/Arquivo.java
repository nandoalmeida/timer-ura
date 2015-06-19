package br.com.vivo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Calendar;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class Arquivo {
	public static File log;
	public static File logDelete;
	public static SmbFile fileSmb;
	public static boolean conexaoDeRede = true;
	private static String dominio = "POLITEC";
	private static String name = "MIS_URA";
	private static String password = "Usuarios2013sim";
	private static String servidor = "10.128.222.105";
	private static String pasta = "/ura/teste/";

	public static String nomeArquivo;
	private static String extensao = "csv";
	public static Calendar calendar;

	private static NtlmPasswordAuthentication auth;
	private static String url;
	public static Long countLinesRemote;

	public static void criarInstanciaFileSmb() {
		try {
			auth = new NtlmPasswordAuthentication(dominio, name, password);
			url = "smb://" + servidor + pasta + nomeArquivoFormatado();
			fileSmb = new SmbFile(url, auth);
			log("Verificando se instancia " + fileSmb.getName() + " já existe ... [Aguarde]");
			fileSmb.exists();
		} catch (SmbException e) {
			conexaoDeRede = false;
			log(e.getMessage());
		} catch (MalformedURLException e) {
			conexaoDeRede = false;
			log(e.getMessage());
		} catch (Exception e) {
			conexaoDeRede = false;
			log(e.getMessage());
		}
	}

	public static void criarArquivoLog() {
		log = new File(nomeArquivoLog());
		if (!log.exists()) {
			Arquivo.log("Criando arquivo de log : " + nomeArquivoLog());
			logDelete = new File(nomeArquivoLogASerRemovido());
			if (logDelete.exists()) {
				Arquivo.log("Arquivo " + nomeArquivoLogASerRemovido() + " existe.");
				if (logDelete.delete()) {
					Arquivo.log("Arquivo " + nomeArquivoLogASerRemovido() + " foi removido com sucesso.");
				} else {
					Arquivo.log("Arquivo " + nomeArquivoLogASerRemovido() + " não pôde ser removido.");
				}
			} else {
				Arquivo.log("Arquivo " + nomeArquivoLogASerRemovido() + " não existe.");
			}
			Arquivo.log("Arquivo de log " + nomeArquivoLog() + " foi criado.");
		} else {
			Arquivo.log("Arquivo de log " + nomeArquivoLog() + " já foi criado.");
		}
	}

	public static void log(String linha) {
		String lLinha = "";
		FileWriter writer;
		try {
			writer = new FileWriter(log.getName(), true);
			lLinha = Calendar.getInstance().getTime() + " " + linha;
			writer.append(lLinha);
			writer.append("\r\n");
			writer.flush();
			writer.close();
			System.out.println(lLinha);
		} catch (IOException e) {
			log(e.getMessage());
		}
	}

	public static void logCount(String linha) {
		FileWriter writer;
		try {
			writer = new FileWriter("logcount.txt", true);
			writer.append(linha);
			writer.append("\t - \t");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			log(e.getMessage());
		}
	}

	/*
	 * public static void escreve(String linha) { SmbFileOutputStream sfos; try
	 * { sfos = new SmbFileOutputStream(fileSmb);
	 * sfos.write(linha.getBytes("UTF-8")); } catch (SmbException e1) {
	 * e1.printStackTrace(); } catch (MalformedURLException e1) {
	 * e1.printStackTrace(); } catch (UnknownHostException e1) {
	 * e1.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } }
	 */

	public static boolean existe() {
		boolean existe = false;
		try {
			existe = fileSmb.exists();
		} catch (SmbException e) {
			conexaoDeRede = false;
			log(e.getMessage());
		}

		if (conexaoDeRede) {
			if (existe) {
				log("Arquivo " + fileSmb.getName() + " já existe na rede");

			} else {
				log("Arquivo " + fileSmb.getName() + " não existe na rede");

			}
		}
		return existe;
	}

	public String getDominio() {
		return dominio;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getServidor() {
		return servidor;
	}

	public String getPasta() {
		return pasta;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public static String nomeArquivoFormatado() {
		Calendar c = Calendar.getInstance();
		calendar = c;
		c.add(Calendar.HOUR, -Task.horasAntes);
		String d = String.format("%02d", c.get(Calendar.DAY_OF_MONTH));
		String m = String.format("%02d", (c.get(Calendar.MONTH) + 1));
		String h = String.format("%02d", c.get(Calendar.HOUR_OF_DAY));
		nomeArquivo = d + m + h + "." + extensao;
		return "REMOVER__" + nomeArquivo;
	}

	public static String nomeArquivoFormatado(int hora) {
		Calendar c = Calendar.getInstance();
		calendar = c;
		String d = String.format("%02d", c.get(Calendar.DAY_OF_MONTH));
		String m = String.format("%02d", (c.get(Calendar.MONTH) + 1));
		String h = String.format("%02d", hora);
		nomeArquivo = d + m + h + "." + extensao;
		return "REMOVER__" + nomeArquivo;
	}

	public static String nomeArquivoLog() {
		Calendar c = Calendar.getInstance();
		String d = String.format("%02d", c.get(Calendar.DAY_OF_MONTH));
		String m = String.format("%02d", (c.get(Calendar.MONTH) + 1));
		String a = String.format("%02d", c.get(Calendar.YEAR));
		nomeArquivo = "LOG_" + a + "_" + m + "_" + d + ".txt";
		return nomeArquivo;
	}

	public static String nomeArquivoLogASerRemovido() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		String d = String.format("%02d", c.get(Calendar.DAY_OF_MONTH));
		String m = String.format("%02d", (c.get(Calendar.MONTH) + 1));
		String a = String.format("%02d", c.get(Calendar.YEAR));
		nomeArquivo = "LOG_" + a + "_" + m + "_" + d + ".txt";
		return nomeArquivo;
	}

	public static String nomeArquivoExcelASerRemovido() {
		Calendar c = calendar;
		c.add(Calendar.HOUR, -Task.horasAntes);
		c.add(Calendar.MONTH, -1);
		String d = String.format("%02d", c.get(Calendar.DAY_OF_MONTH));
		String m = String.format("%02d", (c.get(Calendar.MONTH) + 1));
		String h = String.format("%02d", c.get(Calendar.HOUR_OF_DAY));
		return d + m + h + "." + extensao;
	}

	public static boolean copiarDoLocalParaRede() {
		File file = null;
		file = new File(Arquivo.nomeArquivo);
		InputStream fileInputStream;
		Long tamanhoArquivoRemotoEmBytes = 0L;
		Long tamanhoArquivoLocalEmBytes = file.length();
		boolean copiaRelizadaComSucesso = true;
		try {
			if (!fileSmb.exists()) {
				Arquivo.log("-------------------------------------------------------");
				log("Arquivo " + fileSmb.getName() + " não existe na pasta da rede.");
				log("A cópia do arquivo para a rede iniciou.");
				byte[] buf;
				int len;
				buf = new byte[32 * 1024 * 1024];
				try {
					SmbFileOutputStream smbFileOutputStream = new SmbFileOutputStream(fileSmb);
					fileInputStream = new FileInputStream(file.getAbsolutePath());
					while ((len = fileInputStream.read(buf)) > 0) {
						smbFileOutputStream.write(buf, 0, len);
					}
					tamanhoArquivoRemotoEmBytes = fileSmb.length();
					fileInputStream.close();
					smbFileOutputStream.close();
				} catch (MalformedURLException e) {
					copiaRelizadaComSucesso = false;
					log(e.getMessage());
					deletarArquivoRemoto();
				} catch (FileNotFoundException e) {
					copiaRelizadaComSucesso = false;
					log(e.getMessage());
					deletarArquivoRemoto();
				} catch (IOException e) {
					copiaRelizadaComSucesso = false;
					log(e.getMessage());
					deletarArquivoRemoto();
				} finally {
					log("A cópia do arquivo para a rede finalizou.");
					log("------------------------------------------------------");
					copiaRelizadaComSucesso = validarTamanho(tamanhoArquivoRemotoEmBytes, tamanhoArquivoLocalEmBytes);
					log("------------------------------------------------------");
				}
			} else {
				log("Arquivo já existia na pasta da rede");
			}
		} catch (SmbException e) {
			copiaRelizadaComSucesso = false;
			log(e.getMessage());
		}
		return copiaRelizadaComSucesso;
	}

	private static boolean validarTamanho(Long tamanhoArquivoRemotoEmBytes, Long tamanhoArquivoLocalEmBytes) {
		if (!tamanhoArquivoLocalEmBytes.equals(tamanhoArquivoRemotoEmBytes)) {
			String msgLog = "Arquivos remoto e local possuem tamanhos diferentes em número de bytes. [local: ";
			msgLog += tamanhoArquivoLocalEmBytes.toString();
			msgLog += " , remoto: " + tamanhoArquivoRemotoEmBytes + " ]";
			log(msgLog);
			deletarArquivoRemoto();
			return false;
		} else {
			String msgLog = "Arquivos remoto e local possuem tamanhos iguais em número de bytes. [local: ";
			msgLog += tamanhoArquivoLocalEmBytes.toString();
			msgLog += " , remoto: " + tamanhoArquivoRemotoEmBytes + " ]";
			log(msgLog);
			return true;
		}
	}

	public static boolean deletarArquivoLocal() {
		boolean retorno = true;
		File file = new File(nomeArquivoExcelASerRemovido());
		if (file.exists()) {
			if (file.delete()) {
				retorno = true;
				log("Arquivo local foi deletado com sucesso !");
			} else {
				retorno = false;
				log("Arquivo local não pode ser deletado !");
			}
		}
		return retorno;

	}

	public static void deletarArquivoRemoto() {
		log("Arquivo remoto será removido.");
		try {
			if (fileSmb.exists()) {
				fileSmb.delete();
				log("Arquivo remoto foi removido com sucesso.");
			}
		} catch (SmbException e) {
			log(e.getMessage());
		}
	}

	public static int getBytesSizeOfFile(InputStream is) {
		int size = 0;
		size = getArrayBytes(is).length;
		return size;
	}

	public static byte[] getArrayBytes(InputStream is) {
		int len;
		int size = 1024;
		byte[] buf = {};
		try {
			if (is instanceof ByteArrayInputStream) {
				size = is.available();
				buf = new byte[size];
				len = is.read(buf, 0, size);

			} else {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				buf = new byte[size];
				while ((len = is.read(buf, 0, size)) != -1)
					bos.write(buf, 0, len);
				buf = bos.toByteArray();
			}
		} catch (IOException e) {
			log(e.getMessage());
		}
		return buf;
	}

	public static boolean verificarSeArquivoExiste(String nomeArquivo) {
		boolean result = false;
		auth = new NtlmPasswordAuthentication(dominio, name, password);
		url = "smb://" + servidor + pasta + nomeArquivo;
		try {
			fileSmb = new SmbFile(url, auth);
			result = fileSmb.exists();
		} catch (SmbException e) {
			log(e.getMessage());
		} catch (MalformedURLException e) {
			log(e.getMessage());
		}
		return result;
	}

}
