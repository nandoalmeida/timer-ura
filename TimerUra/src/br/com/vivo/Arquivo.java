package br.com.vivo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
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

	public static void criarArquivoNaRede() {
		try {
			auth = new NtlmPasswordAuthentication(dominio, name, password);
			url = "smb://" + servidor + pasta + nomeArquivoFormatado();
			fileSmb = new SmbFile(url, auth);
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
	

	public static void escreve(String linha) {
		SmbFileOutputStream sfos;
		try {
			sfos = new SmbFileOutputStream(fileSmb);
			sfos.write(linha.getBytes("UTF-8"));
		} catch (SmbException e1) {
			e1.printStackTrace();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean existe() {
		log("Verificando se arquivo " + fileSmb.getName() + " já existe ... [Aguarde]");
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
		c.add(Calendar.HOUR, - Task.horasAntes);
		String d = String.format("%02d", c.get(Calendar.DAY_OF_MONTH));
		String m = String.format("%02d", (c.get(Calendar.MONTH) + 1));
		String h = String.format("%02d", c.get(Calendar.HOUR_OF_DAY));
		nomeArquivo = d + m + h + "." + extensao;
		return nomeArquivo;
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

	public static void copiarDoLocalParaRede() {
		try {
			if (!fileSmb.exists()) {
				log("Arquivo " + fileSmb.getName() + " não existe.");
				log("A cópia do arquivo para a rede iniciou.");
				File file = null;
				InputStream fileInputStream;
				byte[] buf;
				int len;
				buf = new byte[32 * 1024 * 1024];
				try {
					file = new File(Arquivo.nomeArquivo);
					SmbFileOutputStream smbFileOutputStream = new SmbFileOutputStream(fileSmb);
					fileInputStream = new FileInputStream(file.getAbsolutePath());
					while ((len = fileInputStream.read(buf)) > 0) {
						smbFileOutputStream.write(buf, 0, len);
					}
					fileInputStream.close();
					smbFileOutputStream.close();
				} catch (MalformedURLException e) {
					log(e.getMessage());
				} catch (FileNotFoundException e) {
					log(e.getMessage());
				} catch (IOException e) {
					log(e.getMessage());
				}
			} else {
				log("Arquivo já existia na pasta da rede");
			}
		} catch (SmbException e) {
			e.printStackTrace();
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

}
