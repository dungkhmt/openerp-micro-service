package openerp.openerpresourceserver.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import openerp.openerpresourceserver.controller.vm.CompileSourceVM;
import openerp.openerpresourceserver.dto.CodeEditorSourceDTO;
import openerp.openerpresourceserver.dto.CompileSourceDTO;
import openerp.openerpresourceserver.entity.enumeration.ProgrammingLanguage;
import openerp.openerpresourceserver.exception.BadRequestAlertException;
import openerp.openerpresourceserver.service.CodeEditorSourceService;

@RestController
@RequestMapping(value = "/code-editor")
public class CodeEditorSourceController {
    @Value("${CODE_RUNNER_SERVER_URL}")
    private String codeRunnerServerUrl;

    private final CodeEditorSourceService codeEditorSourceService;

    public CodeEditorSourceController(CodeEditorSourceService codeEditorSourceService) {
        this.codeEditorSourceService = codeEditorSourceService;
    }

    @PostMapping(value = "/sources")
    public ResponseEntity<CodeEditorSourceDTO> createAndUpdateSource(Principal principal,
            @RequestBody CodeEditorSourceDTO codeEditorSourceDTO) {
        if (codeEditorSourceDTO.getRoomId() == null) {
            throw new BadRequestAlertException("Id phòng không được để trống");
        }
        if (codeEditorSourceDTO.getLanguage() == null) {
            throw new BadRequestAlertException("Ngôn ngữ lập trình không được để trống");
        }
        return ResponseEntity.ok().body(codeEditorSourceService.save(principal.getName(), codeEditorSourceDTO));
    }

    @GetMapping(value = "/sources/load-source")
    public ResponseEntity<CodeEditorSourceDTO> loadSource(Principal principal,
            @RequestParam(name = "roomId") UUID roomId,
            @RequestParam(name = "language") ProgrammingLanguage language) {
        return ResponseEntity.ok()
                .body(codeEditorSourceService.loadSourceByRoomIdAndLanguage(principal.getName(), roomId, language));
    }

    @PostMapping(value = "/sources/compile")
    public ResponseEntity<CompileSourceDTO> compileSource(@RequestBody CompileSourceVM compileSourceVM,
            HttpServletRequest request) {
        String token = request.getHeader("authorization");
        String url = codeRunnerServerUrl + "/api/ide/" + compileSourceVM.getLanguage();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", token);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("input", compileSourceVM.getInput());
        requestBody.put("source", compileSourceVM.getSource());
        requestBody.put("timeLimit", 2);
        requestBody.put("memoryLimit", 1000000);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<Map<String, Object>>(requestBody, headers);
        CompileSourceDTO compileSourceDTO = new CompileSourceDTO();
        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            String[] splitResponse = responseBody.split("\n");

            if (splitResponse[splitResponse.length - 1].equals("Compile Error")) {
                compileSourceDTO.setIsError(true);
                compileSourceDTO.setIsKill(false);
                compileSourceDTO.setOutput(responseBody.substring(0, responseBody.lastIndexOf("Compile Error")));
                return ResponseEntity.status(400).body(compileSourceDTO);
            }
            if (splitResponse[0].equals("Killed")) {
                compileSourceDTO.setIsKill(true);
                compileSourceDTO.setIsError(true);
                compileSourceDTO.setOutput("Time limit exceeded");
                return ResponseEntity.status(400).body(compileSourceDTO);
            }
            if (splitResponse[0].equals("File size limit exceeded")) {
                compileSourceDTO.setIsKill(true);
                compileSourceDTO.setIsError(true);
                compileSourceDTO.setOutput("File size limit exceeded");
                return ResponseEntity.status(400).body(compileSourceDTO);
            }
            if (splitResponse[0].equals("Segmentation fault")) {
                compileSourceDTO.setIsKill(true);
                compileSourceDTO.setIsError(true);
                compileSourceDTO.setOutput("Segmentation fault");
                return ResponseEntity.status(400).body(compileSourceDTO);
            }

            compileSourceDTO.setIsError(false);
            compileSourceDTO.setIsKill(false);
            compileSourceDTO.setOutput(responseBody.substring(0, responseBody.lastIndexOf("testcasedone")));
            return ResponseEntity.ok(compileSourceDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(compileSourceDTO);
        }

    }
}
