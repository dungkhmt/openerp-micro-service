export const updateLocalStorageData = (dataFromLS, newData, key) => {
  const dataIndex = dataFromLS.findIndex((data) => Number(data.currentPage) === Number(key))
  if (dataIndex > -1) {
    dataFromLS[dataIndex] = { data: newData, currentPage: key }
  } else {
    dataFromLS.push({ data: newData, currentPage: key })
  }

  return dataFromLS
}

export const mergeDrawData = (oldData, newData) => {
  const totalData = [...oldData, ...newData]
  const result = []
  for (let i = 0; i < totalData.length; ++i) {
    if (result.length === 0) {
      result.push(totalData[i])
      continue
    }
    const index = result.findIndex((item) => Number(item.currentPage) === Number(totalData[i].currentPage))
    if (index === -1) {
      result.push(totalData[i])
    } else {
      const totalDrawData = [...result[index].data, ...totalData[i].data]
      const resultData = []
      for (let j = 0; j < totalDrawData.length; ++j) {
        if (resultData.length === 0) {
          resultData.push(totalDrawData[j])
          continue
        }
        const index = resultData.findIndex((item) => item.key === totalDrawData[j].key)
        if (index === -1) {
          resultData.push(totalDrawData[j])
        }
      }

      result[index] = { ...result[index], data: resultData }
    }
  }

  return result
}

export const removeData = (data, page) => {
  //  data: [{ currentPage: 1, data: []}, { currentPage: 2, data: []}]
  let newData = []
  const index = data.findIndex((item) => Number(item.currentPage) === Number(page))
  if (index === -1) {
    if (data.some((item) => Number(item.currentPage) > Number(page))) {
      // decrease currentPage by 1
      data.forEach((item) => {
        if (Number(item.currentPage) < Number(page)) {
          newData.push(item)
        } else {
          newData.push({ ...item, currentPage: Number(item.currentPage) - 1 })
        }
      })
    } else {
      // abort
      newData = data
    }
  } else {
    // remove data in currentPage & decrease currentPage by 1
    for (let i = 0; i < data.length; ++i) {
      if (i === index) {
        continue
      }
      if (Number(data[i].currentPage) < Number(page)) {
        newData.push(data[i])
      } else {
        newData.push({ ...data[i], currentPage: Number(data[i].currentPage) - 1 })
      }
    }
  }

  return newData
}
