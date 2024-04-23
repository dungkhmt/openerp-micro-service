export function buildFilterString(filter) {
  const expressions = filter.items
    .map((f) => {
      const subExpressions = f.items
        .map((i) => {
          if (
            i.field === "" ||
            i.operator === "" ||
            (typeof i.operator === "object" &&
              !i.operator.isUnary &&
              i.value.length === 0)
          ) {
            return null;
          }
          return `${i.field.id}${i.operator.id}${i.value.join(",")}`;
        })
        .filter((i) => i !== null);

      if (subExpressions.length === 0) {
        return null;
      }
      const subQueryString = subExpressions.join(` ${f.condition} `);
      return `( ${subQueryString} )`;
    })
    .filter((f) => f !== null);

  return expressions.length > 0
    ? expressions.join(` ${filter.condition} `)
    : "";
}
