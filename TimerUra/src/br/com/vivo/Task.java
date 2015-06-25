package br.com.vivo;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimerTask;

class Task extends TimerTask {
	public static int[] horasAgendadas;
	public static final int intervaloDeHoras = 3;
	public static final int horaInicialDoDia = 0;
	public static final int horasAntes = 6;

	public Task() {
		preencheVetor();
	}

	public void run() {
		criarArquivoLog();
		log("############################################################################################################################################");
		log("Início da execução da Task.");
		if (horaMarcada()) {
			log("Está na hora marcada.");
			try {
				criarInstanciaFileSmb();
				if (conexaoDeRedeAtiva()) {
					gerarArquivoLocal();
					copiarDoLocalParaARede();
					deletarArquivoLocal();
				}
			} catch (SQLException e) {
				log(e.getMessage());
			} catch (Exception e) {
				log(e.getMessage());
			}
		} else {
			log("Não está na hora marcada.");
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

	private void corrigirArquivosAusentes() {
		int horaAusente = verificarSeArquivosForamGerados();
		log("------------------------------------------------------");
		if (horaAusente < 0) {
			log("Todos os arquivos do dia foram gerados corretamente até agora. \r\n \r\n");
		} else {
			log("Arquivo " + Arquivo.nomeArquivoFormatado(horaAusente) + " será gerado fora da hora marcada.");
			log("Início da geração do arquivo " + Arquivo.nomeArquivoFormatado(horaAusente) + ".");
			gerarArquivoAusente(horaAusente);
			log("Fim da geração do arquivo " + Arquivo.nomeArquivoFormatado(horaAusente) + ".");
			log("------------------------------------------------------");
		}
	}

	private void copiarDoLocalParaARede() {
		if (Arquivo.copiarDoLocalParaRede()) {
			log("O arquivo já foi copiado com sucesso para a pasta na rede.");
			log("-----------------------------------------------------------");

		} else {
			log("Falha no momento de copiar o arquivo para a pasta na rede.");
		}
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

	public int verificarSeArquivosForamGerados() {
		int retorno = -1;
		Calendar dataAtual = Calendar.getInstance();
		int horaAtual = dataAtual.get(Calendar.HOUR_OF_DAY);
		log("...  \r\n \r\n");
		log("------------------------------------------------------");
		log("Verificando se hoje os arquivos foram gerados corretamente ... [Aguarde]");

		for (int i = 0; ((i < horasAgendadas.length) && (horasAgendadas[i] < horaAtual)); i++) {
			if (verificarSeArquivoExiste(Arquivo.nomeArquivoFormatado(horasAgendadas[i]))) {
				log("Arquivo " + Arquivo.nomeArquivoFormatado(horasAgendadas[i]) + " já existe na rede.");
			} else {
				log("Hoje, houve um problema na geração dos arquivos e será corrigido.");
				log("Arquivo " + Arquivo.nomeArquivoFormatado(horasAgendadas[i]) + " ainda não existe na rede.");
				return horasAgendadas[i];
			}
		}
		log("Hoje, todos arquivos foram gerados corretamente até agora.");
		return retorno;
	}

	public void gerarArquivoAusente(int horaInicial) {
		try {
			Relatorio.gerarArquivoAusente(horaInicial);
			copiarDoLocalParaARede();
			deletarArquivoLocal();
		} catch (SQLException e) {
			log(e.getMessage());
		}
	}

	public boolean verificarSeArquivoExiste(String nomeArquivo) {
		return Arquivo.verificarSeArquivoExiste(nomeArquivo);
	}

	private void log(String msg) {
		Arquivo.log(msg);
	}

	private void gerarArquivoLocal() throws SQLException, Exception {
		Relatorio.gerarArquivoLocal();
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
		Arquivo.deletarArquivoLocal();
	}

}
