package openerp.openerpresourceserver.controller;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.model.LogCreate;
import openerp.openerpresourceserver.model.Project;
import openerp.openerpresourceserver.service.ApiService;

@Controller
@Log4j2
public class WebController {
  private final ApiService apiService;

  public WebController(ApiService apiService) {
    this.apiService = apiService;
  }

  // @GetMapping("/")
  // public String home(Model model) {
  // model.addAttribute("credential", new ClientCredential());
  // return "index";
  // }

  @GetMapping("/")
  public String allProjects(Model model) {
    try {
      ResponseEntity<List<Project>> response = apiService.callApi("/all-projects",
          new ParameterizedTypeReference<List<Project>>() {
          });
      List<Project> projects = response.getBody();
      model.addAttribute("projects", projects);
      return "all-projects";
    } catch (WebClientResponseException e) {
      log.error(e);
      switch (e.getStatusCode().value()) {
        case 401:
          model.addAttribute("error", "Invalid client credential");
          return "index";
        case 403:
          return "forbidden";
        default:
          model.addAttribute("error", e.getMessage());
          return "index";
      }
    } catch (RuntimeException e) {
      log.error(e.getMessage());
      e.printStackTrace();
      return "error500";
    }
  }

  @GetMapping("/create-log")
  public String createLog() {
    var body = LogCreate.builder().userId("dungpq").description("test log").build();
    this.apiService.callPostApi("/log/create-log", Void.class, body);
    return "forbidden";
  }
}
