package openerp.openerpresourceserver.service.impl;

import jakarta.ws.rs.core.Response;
import openerp.openerpresourceserver.dto.EmployeeRequestDto;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KeycloakAdminClient implements openerp.openerpresourceserver.service.KeycloakAdminClient {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;


    @Override
    public Keycloak getKeycloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm("smart_delivery")
                .clientId(clientId)
                .username("peter_nguyen74")
                .password("123456")
                .grantType("password")  // Use "password" grant type
                .build();
    }

    @Override
    public void createUserInKeycloak(EmployeeRequestDto employeeRequestDto) {
        Keycloak keycloak = getKeycloakInstance();
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        // Tạo UserRepresentation
        UserRepresentation user = new UserRepresentation();
        user.setUsername(employeeRequestDto.getUsername());
        user.setEmail(employeeRequestDto.getEmail());
        user.setFirstName(employeeRequestDto.getName());
        user.setEnabled(true);

        // Set Attributes
        Map<String, List<String>> attributes = new HashMap<String, List<String>>();
        attributes.put("hubId",Collections.singletonList(employeeRequestDto.getHub().toString()));
        user.setAttributes(attributes);

        // Set credentials
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(employeeRequestDto.getPassword());
        user.setCredentials(Collections.singletonList(credential));

        try (Response response = usersResource.create(user)) {
            if (response.getStatus() != 201) {
                throw new RuntimeException("Không thể tạo người dùng: " + response.readEntity(String.class));
            }
        }

        // Lấy ID người dùng vừa tạo
        String userId = usersResource.search(employeeRequestDto.getUsername()).get(0).getId();

        // Lấy UUID của client "smart"
        String clientUuid = realmResource.clients().findByClientId("smart").get(0).getId();

        // Lấy vai trò từ client
        RoleRepresentation clientRole = realmResource.clients()
                .get(clientUuid)
                .roles()
                .get(employeeRequestDto.getRole().toKeycloakRole()) // Sử dụng ánh xạ
                .toRepresentation();

        // Gán vai trò cho người dùng
        realmResource.users()
                .get(userId)
                .roles()
                .clientLevel(clientUuid)
                .add(Collections.singletonList(clientRole));
    }
    @Override
    public void updateUserInKeycloak(EmployeeRequestDto employeeRequestDto) {
        Keycloak keycloak = getKeycloakInstance();
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        // Tra cứu user trong Keycloak bằng username
        List<UserRepresentation> users = usersResource.search(employeeRequestDto.getUsername(), 0, 1);
        if (users.isEmpty()) {
            throw new RuntimeException("Không tìm thấy người dùng trong Keycloak với username: " + employeeRequestDto.getUsername());
        }
        String keycloakUserId = users.get(0).getId();

        // In ra thông tin user tìm thấy
        System.out.println("User found in Keycloak: " + users.get(0));

        // Tìm người dùng dựa trên ID của Keycloak
        UserRepresentation user = usersResource.get(keycloakUserId).toRepresentation();

        // In ra thông tin đầy đủ của user
        System.out.println("User representation before update: " + user);

        // Cập nhật thông tin người dùng (email và firstName)
        user.setFirstName(employeeRequestDto.getName());
        user.setEmail(employeeRequestDto.getEmail());

        // Cập nhật mật khẩu nếu cần
        if (employeeRequestDto.getPassword() != null && !employeeRequestDto.getPassword().isEmpty()) {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false); // Mật khẩu không tạm thời
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(employeeRequestDto.getPassword());
            // Thêm mật khẩu mới vào thông tin người dùng
            user.setCredentials(Collections.singletonList(credential));
        }

        // Không cập nhật vai trò (role) hoặc username

        // Thực hiện cập nhật trong Keycloak
        usersResource.get(keycloakUserId).update(user);

        // In ra thông tin user sau khi cập nhật
        UserRepresentation updatedUser = usersResource.get(keycloakUserId).toRepresentation();
        System.out.println("User representation after update: " + updatedUser);
    }


}