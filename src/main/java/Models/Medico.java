package Models;

import java.math.BigDecimal;

public class Medico extends Pessoa {
    private String crm;
    private BigDecimal porcentagemParticipacao;
    private String consultorio;
    private Especialidade especialidade;

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public BigDecimal getPorcentagemParticipacao() {
        return porcentagemParticipacao;
    }

    public void setPorcentagemParticipacao(BigDecimal porcentagemParticipacao) {
        this.porcentagemParticipacao = porcentagemParticipacao;
    }

    public String getConsultorio() {
        return consultorio;
    }

    public void setConsultorio(String consultorio) {
        this.consultorio = consultorio;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(Especialidade especialidade) {
        this.especialidade = especialidade;
    }
}
