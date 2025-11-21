package tunutech.api.dtos;

import tunutech.api.model.Project;

import java.util.List;

public class TranslationProjectsDto {
    private List<ProjectResponseDto> waitting;
    private List<ProjectResponseDto> current;
    private List<ProjectResponseDto> completed;

    // Constructeurs
    public TranslationProjectsDto() {}

    public TranslationProjectsDto(List<ProjectResponseDto>waitting,List<ProjectResponseDto>current, List<ProjectResponseDto> completed) {
        this.waitting =waitting ;
        this.current = current;
        this.completed = completed;
    }
    // Getters et Setters
    public List<ProjectResponseDto>getWaitting() { return waitting; }
    public void setWaitting(List<ProjectResponseDto> waitting) { this.waitting = waitting; }

    public List<ProjectResponseDto> getCurrent() { return current; }
    public void setCurrent(List<ProjectResponseDto> current) { this.current = current; }

    public List<ProjectResponseDto> getCompleted() { return completed; }
    public void setCompleted(List<ProjectResponseDto> completed) { this.completed = completed; }
}
