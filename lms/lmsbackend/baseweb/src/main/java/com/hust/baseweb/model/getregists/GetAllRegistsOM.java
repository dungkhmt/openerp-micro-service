package com.hust.baseweb.model.getregists;

import com.hust.baseweb.model.GetAllRolesOM;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAllRegistsOM {

    List<RegistsOM> regists = new ArrayList<>();

    Set<GetAllRolesOM> roles;
}
