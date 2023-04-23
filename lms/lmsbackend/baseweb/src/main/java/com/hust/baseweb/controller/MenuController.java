//package com.hust.baseweb.controller;
//
//import com.hust.baseweb.entity.SecurityPermission;
//import com.hust.baseweb.service.MenuService;
//import lombok.AllArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Deprecated
//@RestController
//@CrossOrigin
//@AllArgsConstructor(onConstructor_ = @Autowired)
//public class MenuController {
//
//    private MenuService menuService;
//
//    /**
//     * > Get all the menu application ids that the user has access to
//     *
//     * @param token The token that is passed in the request header.
//     * @return A set of application ids
//     */
//    @GetMapping("/menu")
//    public ResponseEntity<Set> getMenu(JwtAuthenticationToken token) {
//        List<SecurityPermission> permissions = token
//            .getAuthorities()
//            .stream()
//            .filter(grantedAuthority -> !grantedAuthority
//                .getAuthority()
//                .startsWith("ROLE_GR")) // remove all composite roles
//            .map(grantedAuthority -> { // convert role to permission
//                String permissionId = grantedAuthority.getAuthority().substring(5); // remove prefix "ROLE_"
//                SecurityPermission permission = new SecurityPermission();
//                permission.setPermissionId(permissionId);
//                return permission;
//            })
//            .collect(Collectors.toList());
//
//        return ResponseEntity.ok().body(menuService.getMenu(permissions));
//    }
//}
