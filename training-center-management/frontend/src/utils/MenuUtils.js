export function buildMapPathMenu(menuConfig) {
  let listMenu = [];
  let map = new Map();

  for (let i = 0; i < menuConfig.length; i++) {
    listMenu.push(...buildFromItem(menuConfig[i]));
  }

  for (let i = 0; i < listMenu.length; i++) {
    let menu = listMenu[i];
    map.set(menu.path, menu);
  }

  return map;
}

function buildFromItem(config) {
  let ret = [];

  if (config.path && config.path !== "") {
    ret.push(config);
  }

  if (config.child?.length > 0) {
    let parent = Object.assign({}, config);
    let childs = config.child;

    parent["child"] = null;

    for (let i = 0; i < childs.length; i++) {
      let menu = childs[i];
      menu["parent"] = parent;

      ret.push(...buildFromItem(menu));
    }
  }

  return ret;
}
