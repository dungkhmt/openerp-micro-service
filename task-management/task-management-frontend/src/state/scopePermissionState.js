import { hookstate, useHookstate } from "@hookstate/core";
import { request } from "../api";


export const scopePermissionState = hookstate({
  isFetching: false,
  isFetched: false,
  permittedScopeIds: new Set(),
});


export function useScopePermissionState() {
  return useHookstate(scopePermissionState);
}


export function fetchPermittedScopes() {
  const state = scopePermissionState;

  if (state.isFetching.get()) {
    console.log("Уже выполняется получение разрешенных областей видимости.");
    return;
  }

  state.isFetching.set(true);
  state.isFetched.set(false);

  request(
    "get", // HTTP метод
    "/entity-authorization/SCOPE_",
    (res) => {
      const ids = Array.isArray(res.data) ? res.data : [];
      state.merge({
        permittedScopeIds: new Set(ids),
        isFetching: false,
        isFetched: true,
      });
    },
    {
      onError: (err) => {
        console.error("Не удалось получить разрешенные области видимости:", err);
        state.merge({
          isFetching: false,
          isFetched: false,
          permittedScopeIds: new Set(),
        });
      },
      401: () => {
        console.warn("Не авторизован (401) для получения разрешенных областей видимости.");
        state.merge({
          isFetching: false,
          isFetched: false,
          permittedScopeIds: new Set(),
        });
      },
    }
  );
}