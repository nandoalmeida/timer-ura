package br.com.vivo;

import java.io.File;
import java.io.FileFilter;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimerTask;

class Task extends TimerTask {
	public static int[] horasAgendadas;
	public static final int intervaloDeHoras = 3;
	public static final int horaInicialDoDia = 0;
	public static final int horasAntes = 6;
	public static final int quantidadeDeUltimasHorasAAvaliar = 48;

	public Task() {
		File pasta = new File("arquivos_csv");
		if (!pasta.exists()) {
			pasta.mkdir();
		}
		preencheVetor();
	}

	public void run() {
		criarArquivoLog();
		log("############################################################################################################################################");
		log("In�cio da execu��o da Task.");
		corrigirFalhas();
		if (horaMarcada()) {
		//if (true) {
			log("Est� na hora marcada.");
			try {
				criarInstanciaFileSmb();
				if (conexaoDeRedeAtiva()) {
					if (gerarArquivoLocal()) {
						copiarDoLocalParaARede();
						deletarArquivoLocal();
					}
				}
			} catch (SQLException e) {
				log(e.getMessage());
			} catch (Exception e) {
				log(e.getMessage());
			}
		} else {
			log("N�o est� na hora marcada.");
			corrigirArquivosAusentes();
		}
		log("A task finalizou.   ");
		log("############################################################################################################################################ \r\n \r\n");
	}

	public static void preencheVetor() {

		horasAgendadas = new int[24 / intervaloDeHoras];
		horasAgendadas[0] = horaInicialDoDia;
		int i = 1;
		while (i < horasAgendadas.length) {
			if ((horasAgendadas[i - 1] + intervaloDeHoras) < 24) {
				horasAgendadas[i] = horasAgendadas[i - 1] + intervaloDeHoras;
			} else {
				horasAgendadas[i] = (horasAgendadas[i - 1] + intervaloDeHoras) - 24;
			}
			i++;
		}

		System.out.print("{");
		System.out.print(horasAgendadas[0]);
		for (int j = 1; j < horasAgendadas.length; j++) {
			System.out.print("," + horasAgendadas[j]);
		}
		System.out.println("}");
	}

	private void corrigirFalhas() {
		log("Corrigindo arquivos que ainda estavam presentes localmente.");
		String arquivoAusente = encontrarArquivosQueNaoForamGeradosCorretamente();
		log("----------------------------------------------------------------------");
		if (!arquivoAusente.equals("")) {
			verificarSeArquivoExiste(arquivoAusente);
			log("Arquivo " + arquivoAusente + " ser� gerado fora da hora marcada.");
			log("In�cio da gera��o do arquivo " + arquivoAusente + ".");
			gerarArquivoAusente(arquivoAusente);
			log("Fim da gera��o do arquivo " + arquivoAusente + ".");
			log("----------------------------------------------------------------------");
		}
		log("Finalizando corre��o.");
	}

	private String encontrarArquivosQueNaoForamGeradosCorretamente() {
		FileFilter filter = new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".csv");
			}
		};
		File dir = new File(Arquivo.pastaLocal);
		File[] files = dir.listFiles(filter);
		for (File file : files) {
			return file.getName();
		}
		
		/*for (int i = 0; i < files.length; i++) {
			return files[i].getName();
		}*/
		return "";
	}

	private void corrigirArquivosAusentes() {
		Calendar arquivoAusente = verificarSeArquivosDasUltimasHorasForamGerados(quantidadeDeUltimasHorasAAvaliar);
		log("----------------------------------------------------------------------");
		if (arquivoAusente != null) {
			log("Arquivo " + Arquivo.nomeArquivoFormatado(arquivoAusente) + " ser� gerado fora da hora marcada.");
			log("In�cio da gera��o do arquivo " + Arquivo.nomeArquivoFormatado(arquivoAusente) + ".");
			gerarArquivoAusente(arquivoAusente);
			log("Fim da gera��o do arquivo " + Arquivo.nomeArquivoFormatado(arquivoAusente) + ".");
			log("----------------------------------------------------------------------");
		}
	}

	private boolean copiarDoLocalParaARede() {
		boolean sucesso = Arquivo.copiarDoLocalParaRede();
		if (sucesso) {
			log("O arquivo j� foi copiado com sucesso para a pasta na rede.");
			log("----------------------------------------------------------------------");
		} else {
			log("Falha no momento de copiar o arquivo para a pasta na rede.");
		}
		return sucesso;
	}

	private boolean horaMarcada() {
		Calendar dataAtual = Calendar.getInstance();
		int horaAtual = dataAtual.get(Calendar.HOUR_OF_DAY);
		for (int i = 0; i < horasAgendadas.length; i++) {
			if (horasAgendadas[i] == horaAtual) {
				return true;
			}
		}
		return false;
	}

	private boolean horaMarcada(Calendar data) {
		int hora = data.get(Calendar.HOUR_OF_DAY);
		for (int i = 0; i < horasAgendadas.length; i++) {
			if (horasAgendadas[i] == hora) {
				return true;
			}
		}
		return false;
	}

	public Calendar verificarSeArquivosDasUltimasHorasForamGerados(int quantidadeDeUltimasHorasAAvaliar) {
		log("----------------------------------------------------------------------");
		log("Verificando se os arquivos das �ltimas " + quantidadeDeUltimasHorasAAvaliar + " horas foram gerados corretamente ... [Aguarde]");
		Calendar dataProgramada = Calendar.getInstance();
		for (int hora = quantidadeDeUltimasHorasAAvaliar; hora > horasAntes; hora--) {
			dataProgramada = Calendar.getInstance();
			dataProgramada.add(Calendar.HOUR_OF_DAY, -hora);
			if (horaMarcada(dataProgramada)) {
				int existe = verificarSeArquivoExiste(Arquivo.nomeArquivoFormatado(dataProgramada));
				if (existe == 1) {
					log("Arquivo " + Arquivo.nomeArquivoFormatado(dataProgramada) + " j� existe na rede.");
				} else if (existe == 0) {
					log("Houve um problema na gera��o dos arquivos e ser� corrigido.");
					log("Arquivo " + Arquivo.nomeArquivoFormatado(dataProgramada) + " ainda n�o existe na rede.");
					return dataProgramada;
				} else {
					log("Houve um problema na consulta dos arquivos, tentaremos mais tarde.");
					return null;
				}
			}
		}
		log("Todos arquivos foram gerados corretamente at� agora.");
		return null;
	}

	/*public void gerarArquivoAusente(int horaInicial) {
		try {
			Relatorio.gerarArquivoAusente(horaInicial);
			copiarDoLocalParaARede();
			deletarArquivoLocal();
		} catch (SQLException e) {
			log(e.getMessage());
			Arquivo.deletarArquivoRemoto();
		}
	}*/

	public void gerarArquivoAusente(String nomeArquivo) {
		try {
			if (Relatorio.gerarArquivoAusente(nomeArquivo)) {
				if (copiarDoLocalParaARede()) {
					deletarArquivoLocal(nomeArquivo);
				}
			}
		} catch (SQLException e) {
			log(e.getMessage());
			Arquivo.deletarArquivoRemoto();
		}
	}

	public void gerarArquivoAusente(Calendar arquivoAusente) {
		try {
			if (Relatorio.gerarArquivoAusente(arquivoAusente)){
				if (copiarDoLocalParaARede()){
					deletarArquivoLocal(arquivoAusente);
				}
			}
		} catch (SQLException e) {
			log(e.getMessage());
		}
	}

	/*
	 * public boolean verificarSeArquivoExiste2(String nomeArquivo) { return
	 * Arquivo.verificarSeArquivoExiste(nomeArquivo); }
	 */
	public int verificarSeArquivoExiste(String nomeArquivo) {
		return Arquivo.verificarSeArquivoExiste(nomeArquivo);
	}

	private void log(String msg) {
		Arquivo.log(msg);
	}

	private boolean gerarArquivoLocal() throws SQLException, Exception {
		return Relatorio.gerarArquivoLocal();
	}

	private void criarArquivoLog() {
		Arquivo.criarArquivoLog();
	}

	private void criarInstanciaFileSmb() {
		Arquivo.criarInstanciaFileSmb();
	}

	private boolean conexaoDeRedeAtiva() {
		return Arquivo.conexaoDeRede;
	}

	private void deletarArquivoLocal() {
		Arquivo.log("Removendo arquivo local");
		Arquivo.deletarArquivoLocal();
	}

	private void deletarArquivoLocal(String nomeArquivo) {
		Arquivo.log("Removendo arquivo local");
		Arquivo.deletarArquivoLocal(nomeArquivo);
	}

	private void deletarArquivoLocal(Calendar arquivoAusente) {
		Arquivo.deletarArquivoLocal(arquivoAusente);
	}

}
