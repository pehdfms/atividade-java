package Models;

import java.time.LocalDateTime;

public abstract class AbstractEntity {
    private Long id;
    private LocalDateTime cadastro;
    private LocalDateTime atualizado;
    private LocalDateTime desativado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCadastro() {
        return cadastro;
    }

    public void setCadastro(LocalDateTime cadastro) {
        this.cadastro = cadastro;
    }

    public LocalDateTime getAtualizado() {
        return atualizado;
    }

    public void setAtualizado(LocalDateTime atualizado) {
        this.atualizado = atualizado;
    }

    public LocalDateTime getDesativado() {
        return desativado;
    }

    public Boolean isDesativado() {
        return desativado != null;
    }

    public void setDesativado(LocalDateTime desativado) {
        this.desativado = desativado;
    }
}
