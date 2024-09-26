import React, {useContext, useEffect, useMemo, useRef, useState} from 'react'
import {KEYS, ROLE_STATUS, SOCKET_IO_EVENTS, TOOL} from '../../../../utils/whiteboard/constants'
import {request} from '../../../../api'
import {useParams} from 'react-router'
import {toast} from 'react-toastify'
import Slider from 'react-slick'
import {SocketContext} from '../../../../utils/whiteboard/context/SocketContext'
import {removeData} from '../../../../utils/whiteboard/localStorage'
import {Dropdown} from '../Dropdown'
import 'slick-carousel/slick/slick.css'
import 'slick-carousel/slick/slick-theme.css'
import {ListParticipantDropdown} from '../ListParticipantDropdown'
import {Button, FormControl, InputLabel, makeStyles, MenuItem, Select} from '@material-ui/core'
import {CustomStage} from './CustomStage'

const useStyles = makeStyles((theme) => ({
  container: {
    position: 'relative',
  },
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120,
  },
  selectEmpty: {
    marginTop: theme.spacing(2),
  },
  optionWrapper: {
    display: 'flex',
    alignItems: 'center',
    columnGap: 12,
    fontSize: 12,
  },
}))

const generatePages = (totalPage) => {
  const pages = []
  for (let i = 0; i < totalPage; ++i) {
    pages.push({ id: i })
  }
  return pages
}

export const MainBoard = React.memo(() => {
  const { socket } = useContext(SocketContext)
  const { whiteboardId } = useParams()
  const classes = useStyles()

  const [pageNow, setPageNow] = useState(Number(localStorage.getItem(KEYS.CURRENT_PAGE) ?? 1))
  const [pages, setPages] = useState(() => generatePages(1))
  const [tool, setTool] = useState(TOOL.POINTER)
  const [strokeDraw, setStrokeDraw] = useState({
    strokeWidth: 5,
    color: '#df4b26',
  })

  const [roleStatus, setRoleStatus] = useState({
    roleId: ROLE_STATUS.READ,
    statusId: ROLE_STATUS.IDLE,
    isCreatedUser: false,
  })
  const [pendingDrawRequestList, setPendingDrawRequestList] = useState([])
  const [listParticipant, setListParticipant] = useState([])

  const parentRef = useRef(null)
  const slideRef = useRef(null)
  // const totalPageRef = useRef(1)

  const isAbleToDraw = roleStatus.roleId === ROLE_STATUS.WRITE && roleStatus.statusId === ROLE_STATUS.ACCEPTED

  useEffect(() => {
    socket.on(SOCKET_IO_EVENTS.ON_ADD_NEW_PAGE, ({ newPage, currentWhiteboardId, changePage, totalPage }) => {
      if (whiteboardId !== currentWhiteboardId) {
        return
      }
      if (!changePage) {
        setPages((prev) => [...prev, newPage])
      }
      localStorage.setItem(KEYS.CURRENT_PAGE, newPage.id)
      if (totalPage) {
        localStorage.setItem(KEYS.TOTAL_PAGE, totalPage)
      }
      setPageNow(newPage.id)
      if (slideRef.current) {
        slideRef.current.slickGoTo(newPage.id - 1)
      }
    })

    socket.on(SOCKET_IO_EVENTS.ON_DELETE_PAGE, ({ pageId, currentWhiteboardId, newDrawData, totalPage }) => {
      if (whiteboardId !== currentWhiteboardId) {
        return
      }
      setPages((prev) =>
        prev.reduce((acc, item) => {
          if (item.id < Number(pageId)) {
            acc.push(item)
          } else {
            if (item.id === Number(pageId)) {
              return acc
            }
            acc.push({ id: item.id - 1 })
          }
          return acc
        }, []),
      )

      localStorage.setItem(KEYS.CURRENT_PAGE, pageId === 1 ? 1 : pageId - 1)
      localStorage.setItem(KEYS.TOTAL_PAGE, totalPage)
      // localStorage.setItem(KEYS.CURRENT_STAGE_ID, `${stageRef.current[pageId === 1 ? 1 : pageId - 1]._id}`)
      if (slideRef.current) {
        slideRef.current.slickGoTo(pageId === 1 ? 0 : pageId - 2)
      }

      localStorage.setItem(KEYS.DRAW_DATA_LOCAL_STORAGE, JSON.stringify(newDrawData))
      socket.emit(SOCKET_IO_EVENTS.CHECK_LOCAL_STORAGE, { currentWhiteboardId: whiteboardId })
      setPageNow(pageId === 1 ? 1 : pageId - 1)
    })

    socket.on(SOCKET_IO_EVENTS.ON_CHANGE_STROKE_DRAW, ({ data, currentWhiteboardId }) => {
      if (whiteboardId !== currentWhiteboardId) {
        return
      }
      setStrokeDraw(data)
    })

    // add user to whiteboard here
    void (async () => {
      await request(
        'put',
        `/whiteboards/user/${whiteboardId}`,
        (res) => {
          if (res.status === 200) {
            setRoleStatus({
              roleId: res?.data?.roleId,
              statusId: res?.data?.statusId,
              isCreatedUser: res?.data?.isCreatedUser ?? false,
            })
            localStorage.setItem(KEYS.USER_ID, res?.data?.userId)
            socket.emit(SOCKET_IO_EVENTS.CONNECT_TO_WHITEBOARD, { currentWhiteboardId: whiteboardId })
          }
        },
        {},
        { roleId: ROLE_STATUS.READ, statusId: ROLE_STATUS.IDLE },
      )

      await request(
        'get',
        `/whiteboards/detail/${whiteboardId}`,
        (res) => {
          const currentPage = res?.data?.totalPage || 1
          localStorage.setItem(KEYS.TOTAL_PAGE, currentPage)
          localStorage.setItem(KEYS.CURRENT_PAGE, currentPage)
          setPageNow(currentPage)
          const newPages = []
          for (let i = 1; i <= currentPage; ++i) {
            newPages.push({ id: i })
          }
          setPages(newPages)
          if (slideRef.current) {
            slideRef.current.slickGoTo(currentPage - 1)
          }

          // const drawingData = JSON.parse(localStorage.getItem(KEYS.DRAW_DATA_LOCAL_STORAGE))
          // if (
          //   !drawingData ||
          //   (typeof drawingData.whiteboardId !== 'undefined' && drawingData.whiteboardId !== whiteboardId)
          // ) {
          //   localStorage.setItem(
          //     KEYS.DRAW_DATA_LOCAL_STORAGE,
          //     JSON.stringify({ whiteboardId, ...JSON.parse(res?.data?.data || '{}') }),
          //     )
          //     socket.emit(SOCKET_IO_EVENTS.CHECK_LOCAL_STORAGE, { currentWhiteboardId: whiteboardId })
          //     return
          //   }
          localStorage.removeItem(KEYS.DRAW_DATA_LOCAL_STORAGE)
          const totalData = JSON.parse(res?.data?.data) || { lines: [], rectangle: [], circle: [], text: [] }
          totalData.whiteboardId = whiteboardId
          totalData.lines = totalData.lines || []
          totalData.rectangle = totalData.rectangle || []
          totalData.circle = totalData.circle || []
          totalData.text = totalData.text || []

          localStorage.setItem(KEYS.DRAW_DATA_LOCAL_STORAGE, JSON.stringify(totalData))
          socket.emit(SOCKET_IO_EVENTS.CHECK_LOCAL_STORAGE, { currentWhiteboardId: whiteboardId })
        },
        {},
        {},
      )
    })()

    return () => {
      socket.off(SOCKET_IO_EVENTS.ON_ADD_NEW_PAGE)
      socket.off(SOCKET_IO_EVENTS.ON_DELETE_PAGE)
    }
  }, [whiteboardId, socket])

  useEffect(() => {
    socket.on(SOCKET_IO_EVENTS.ON_REQUEST_DRAW, async (data) => {
      if (roleStatus.isCreatedUser) {
        await request(
          'get',
          `/whiteboards/user/${data.currentWhiteboardId}/list-pending`,
          (res) => {
            if (res?.data?.addUserToWhiteboardResultModelList?.length > 0) {
              setPendingDrawRequestList(res.data.addUserToWhiteboardResultModelList)
            }
            toast.info(`${data.userId ?? 'User'} đang yêu cầu quyền vẽ.`, {
              position: toast.POSITION.BOTTOM_RIGHT,
            })
          },
          {},
          {},
        )
      } else {
        if (data?.isSuccess) {
          toast.success(`${data.userId ?? 'User'} đã được chấp thuận quyền vẽ.`, {
            position: toast.POSITION.BOTTOM_RIGHT,
          })
        } else {
          toast.error(`${data.userId ?? 'User'} đã bị từ chối/hủy quyền vẽ.`, {
            position: toast.POSITION.BOTTOM_RIGHT,
          })
        }
        await request(
          'put',
          `/whiteboards/user/${whiteboardId}`,
          (res) => {
            if (res.status === 200) {
              setRoleStatus({
                roleId: res?.data?.roleId,
                statusId: res?.data?.statusId,
                isCreatedUser: res?.data?.isCreatedUser ?? false,
              })
            }
          },
          {},
          { roleId: ROLE_STATUS.READ, statusId: ROLE_STATUS.IDLE },
        )
      }
    })

    void (async () => {
      if (roleStatus.isCreatedUser) {
        await request(
          'get',
          `/whiteboards/user/${whiteboardId}/list-pending`,
          (res) => {
            if (res?.data?.addUserToWhiteboardResultModelList?.length > 0) {
              setPendingDrawRequestList(res.data.addUserToWhiteboardResultModelList)
            }
          },
          {},
          {},
        )
      }
    })()

    void getListUsers()

    return () => {
      socket.off(SOCKET_IO_EVENTS.ON_REQUEST_DRAW)
    }
  }, [roleStatus.isCreatedUser, socket, whiteboardId])

  const getListUsers = async () => {
    if (roleStatus.isCreatedUser) {
      await request(
        'get',
        `/whiteboards/user/${whiteboardId}/list-users`,
        (res) => {
          if (res?.data?.length > 0) {
            setListParticipant(res.data)
          }
        },
        {},
        {},
      )
    }
  }

  useEffect(() => {
    void (async () => {
      if (roleStatus.isCreatedUser) {
        await request(
          'get',
          `/whiteboards/user/${whiteboardId}/list-pending`,
          (res) => {
            if (res?.data?.addUserToWhiteboardResultModelList?.length > 0) {
              setPendingDrawRequestList(res.data.addUserToWhiteboardResultModelList)
            }
          },
          {},
          {},
        )
      }
    })()
  }, [roleStatus.isCreatedUser, whiteboardId])

  const onSaveWhiteboardData = async () => {
    // TODO: modify backend API
    const drawData = JSON.parse(localStorage.getItem(KEYS.DRAW_DATA_LOCAL_STORAGE) || '{}')
    const requestBody = {
      whiteboardId,
      data: JSON.stringify(drawData),
      totalPage: pages.length,
    }
    await request(
      'post',
      '/whiteboards/save',
      () => {
        toast.success('Lưu dữ liệu bảng viết thành công.', {
          position: toast.POSITION.BOTTOM_RIGHT,
        })
      },
      (error) => console.error(error),
      requestBody,
    )
  }

  const onAddNewPage = () => {
    if (tool !== TOOL.POINTER) {
      toast.info('Bạn cần đổi sang công cụ chuột.')
      return
    }
    const currentPagesLength = pages.length
    setPages((prev) => [...prev, { id: currentPagesLength + 1 }])
    setPageNow(currentPagesLength + 1)
    slideRef.current.slickGoTo(currentPagesLength)
    localStorage.setItem(KEYS.CURRENT_PAGE, currentPagesLength + 1)
    localStorage.setItem(KEYS.TOTAL_PAGE, currentPagesLength + 1)

    socket.emit(SOCKET_IO_EVENTS.ADD_NEW_PAGE, {
      newPage: { id: currentPagesLength + 1 },
      currentWhiteboardId: whiteboardId,
      changePage: false,
      totalPage: currentPagesLength + 1,
    })
  }

  const onDeletePage = () => {
    const drawData = JSON.parse(localStorage.getItem(KEYS.DRAW_DATA_LOCAL_STORAGE) || '{}')
    const currentPage = localStorage.getItem(KEYS.CURRENT_PAGE)

    const newDrawData = { lines: [], rectangle: [], circle: [], text: [] }
    newDrawData.lines = removeData(drawData.lines || [], currentPage)
    newDrawData.rectangle = removeData(drawData.rectangle || [], currentPage)
    newDrawData.circle = removeData(drawData.circle || [], currentPage)
    newDrawData.text = removeData(drawData.text || [], currentPage)

    if (Number(currentPage) === 1) {
      localStorage.setItem(KEYS.CURRENT_PAGE, 1)
      setPageNow(1)
    } else {
      localStorage.setItem(KEYS.CURRENT_PAGE, Number(currentPage) - 1)
      setPageNow((prev) => prev - 1)
    }
    localStorage.setItem(KEYS.TOTAL_PAGE, pages.length - 1)
    localStorage.setItem(KEYS.DRAW_DATA_LOCAL_STORAGE, JSON.stringify(newDrawData))

    const newPages = pages.reduce((acc, item) => {
      if (item.id < Number(currentPage)) {
        acc.push(item)
      } else {
        if (item.id === Number(currentPage)) {
          return acc
        }
        acc.push({ id: item.id - 1 })
      }
      return acc
    }, [])
    setPages(newPages)
    if (slideRef.current) {
      slideRef.current.slickGoTo(Number(currentPage) === 1 ? 1 : Number(currentPage) - 2)
    }

    // localStorage.setItem(
    //   KEYS.CURRENT_STAGE_ID,
    //   `${stageRef.current[Number(currentPage) === 1 ? 0 : Number(currentPage) - 2]._id}`,
    // )
    socket.emit(SOCKET_IO_EVENTS.DELETE_PAGE, {
      pageId: Number(currentPage),
      currentWhiteboardId: whiteboardId,
      newDrawData,
      totalPage: pages.length - 1,
    })
    socket.emit(SOCKET_IO_EVENTS.CHECK_LOCAL_STORAGE, { currentWhiteboardId: whiteboardId })
    toast.success('Xóa trang vẽ thành công.', {
      position: toast.POSITION.BOTTOM_RIGHT,
    })
  }

  const onPrevPage = () => {
    if (Number(pageNow) === 1) {
      return
    }
    localStorage.setItem(KEYS.CURRENT_PAGE, Number(pageNow) - 1)
    // localStorage.setItem(KEYS.CURRENT_STAGE_ID, `${stageRef.current[Number(pageNow) - 2]._id}`)
    socket.emit(SOCKET_IO_EVENTS.ADD_NEW_PAGE, {
      currentWhiteboardId: whiteboardId,
      newPage: { id: Number(pageNow) - 1 },
      changePage: true,
    })
    if (slideRef.current) {
      slideRef.current.slickGoTo(Number(pageNow) - 2)
    }
    setPageNow((prev) => prev - 1)
    // socket.emit(SOCKET_IO_EVENTS.CHECK_LOCAL_STORAGE, { currentWhiteboardId: whiteboardId })
  }

  const onNextPage = () => {
    if (Number(pageNow) >= pages.length) {
      return
    }
    socket.emit(SOCKET_IO_EVENTS.ADD_NEW_PAGE, {
      currentWhiteboardId: whiteboardId,
      newPage: { id: Number(pageNow) + 1 },
      changePage: true,
    })

    localStorage.setItem(KEYS.CURRENT_PAGE, Number(pageNow) + 1)
    // localStorage.setItem(KEYS.CURRENT_STAGE_ID, `${stageRef.current[Number(pageNow)]._id}`)
    if (slideRef.current) {
      slideRef.current.slickGoTo(Number(pageNow))
    }
    setPageNow((prev) => prev + 1)
  }

  // const onDrawDone = (tool) =>
  //   setEventPointer((prev) => ({ ...prev, [tool]: { eventType: null, pointerPosition: { x: 0, y: 0 } } })) - 1

  const onRequestDraw = async () => {
    // call API with write-pending
    const userId = localStorage.getItem(KEYS.USER_ID)
    if (!userId) {
      toast.error('Thiếu userId.', {
        position: toast.POSITION.BOTTOM_RIGHT,
      })
      return
    }

    await request(
      'post',
      `/whiteboards/user/${whiteboardId}`,
      (res) => {
        if (res.status === 200) {
          setRoleStatus({
            roleId: res?.data?.roleId,
            statusId: res?.data?.statusId,
          })
          toast.success('Gửi yêu cầu thành công.', {
            position: toast.POSITION.BOTTOM_RIGHT,
          })
          // TODO: Notify to admin
          socket.emit(SOCKET_IO_EVENTS.REQUEST_DRAW, { userId, currentWhiteboardId: whiteboardId })
        }
      },
      {},
      { userId, roleId: ROLE_STATUS.WRITE, statusId: ROLE_STATUS.PENDING },
    )
  }

  const approveRequest = async (item) => {
    // call API with write-accepted
    await request(
      'post',
      `/whiteboards/user/${whiteboardId}`,
      async (res) => {
        if (res.status === 200) {
          toast.info('Yêu cầu vẽ đã được chấp thuận.', {
            position: toast.POSITION.BOTTOM_RIGHT,
          })
          // TODO: Notify to this user
          socket.emit(SOCKET_IO_EVENTS.REQUEST_DRAW, {
            userId: item.userId,
            isSuccess: true,
            currentWhiteboardId: whiteboardId,
          })
          void getListUsers()
          await request(
            'get',
            `/whiteboards/user/${whiteboardId}/list-pending`,
            (res) => {
              if (res?.data?.addUserToWhiteboardResultModelList) {
                setPendingDrawRequestList(res.data.addUserToWhiteboardResultModelList)
              }
            },
            {},
            {},
          )
        }
      },
      {},
      { userId: item.userId, roleId: ROLE_STATUS.WRITE, statusId: ROLE_STATUS.ACCEPTED },
    )
  }

  const rejectRequest = async (item) => {
    // call API with write-accepted
    await request(
      'post',
      `/whiteboards/user/${whiteboardId}`,
      async (res) => {
        if (res.status === 200) {
          toast.info('Yêu cầu vẽ đã bị từ chối/huỷ.', {
            position: toast.POSITION.BOTTOM_RIGHT,
          })
          // TODO: Notify to this user
          socket.emit(SOCKET_IO_EVENTS.REQUEST_DRAW, {
            userId: item.userId,
            isSuccess: false,
            currentWhiteboardId: whiteboardId,
          })
          void getListUsers()
          await request(
            'get',
            `/whiteboards/user/${whiteboardId}/list-pending`,
            (res) => {
              if (res?.data?.addUserToWhiteboardResultModelList) {
                setPendingDrawRequestList(res.data.addUserToWhiteboardResultModelList)
              }
            },
            {},
            {},
          )
        }
      },
      {},
      { userId: item.userId, roleId: ROLE_STATUS.WRITE, statusId: ROLE_STATUS.REJECTED },
    )
  }

  const onChangeStrokeDraw = (e) => {
    setStrokeDraw((prev) => ({
      ...prev,
      [e.target.name]: e.target.value.includes('#') ? e.target.value : Number(e.target.value),
    }))
    socket.emit(SOCKET_IO_EVENTS.CHANGE_STROKE_DRAW, {
      data: { ...strokeDraw, [e.target.name]: e.target.value.includes('#') ? e.target.value : Number(e.target.value) },
      currentWhiteboardId: whiteboardId,
    })
  }

  const settings = useMemo(
    () => ({
      arrows: false,
      className: 'slider-parent',
      dots: false,
      slidesToScroll: 1,
      infinite: false,
      vertical: true,
      verticalSwiping: true,
      draggable: false,
      // beforeChange: onBeforeChange,
      // afterChange: onAfterChange,
    }),
    [],
  )

  return (
    <div className={classes.container}>
      {isAbleToDraw ? (
        <div className={classes.optionWrapper}>
          <Button variant="outlined" color="secondary" onClick={onPrevPage} disabled={pageNow === 1}>
            Trang trước
          </Button>
          <Button variant="outlined" color="primary" onClick={onNextPage} disabled={pageNow >= pages.length}>
            Trang sau
          </Button>
          <label htmlFor="color">Màu nét vẽ</label>
          <input id="color" name="color" type="color" value={strokeDraw.color} onChange={onChangeStrokeDraw} />
          <label htmlFor="strokeWidth">Độ dày nét vẽ</label>
          <input
            type="range"
            max={15}
            min={0}
            step={0.5}
            name="strokeWidth"
            value={strokeDraw.strokeWidth}
            onChange={onChangeStrokeDraw}
            aria-labelledby="strokeWidth"
          />
          <FormControl className={classes.formControl}>
            <InputLabel id="tool">Công cụ</InputLabel>
            <Select labelId="tool" value={tool} onChange={(e) => setTool(e.target.value)}>
              <MenuItem value={TOOL.POINTER}>Chuột</MenuItem>
              <MenuItem value={TOOL.PEN}>Bút</MenuItem>
              <MenuItem value={TOOL.RECTANGLE}>Hình chữ nhật</MenuItem>
              <MenuItem value={TOOL.CIRCLE}>Hình tròn</MenuItem>
              <MenuItem value={TOOL.ERASER}>Tẩy</MenuItem>
              <MenuItem value={TOOL.TEXT}>Chữ</MenuItem>
            </Select>
          </FormControl>
          <Button variant="contained" color="primary" onClick={onSaveWhiteboardData}>
            Lưu
          </Button>
          <Button variant="contained" color="primary" onClick={onAddNewPage}>
            Thêm trang mới
          </Button>
          {pages.length >= 2 && (
            <Button variant="contained" color="secondary" onClick={onDeletePage}>
              Xóa trang {pageNow}
            </Button>
          )}
          {pendingDrawRequestList.length > 0 && (
            <Dropdown
              pendingList={pendingDrawRequestList}
              onApproveRequest={approveRequest}
              onRejectRequest={rejectRequest}
            />
          )}
          {listParticipant.length > 0 && (
            <ListParticipantDropdown list={listParticipant} onRejectRequest={rejectRequest} />
          )}
        </div>
      ) : roleStatus.statusId === ROLE_STATUS.PENDING ? (
        <Button variant="contained" disabled>
          Đã gửi yêu cầu. Vui lòng đợi.
        </Button>
      ) : (
        <Button variant="contained" color="primary" onClick={onRequestDraw}>
          Yêu cầu quyền vẽ
        </Button>
      )}

      <div
        id="slider-grand"
        style={{ width: 'calc(100% - 5px)', height: 'calc(100vh - 195px)', position: 'relative' }}
        ref={parentRef}
      >
        <Slider ref={slideRef} {...settings}>
          {pages.map((page) => (
            <div id={`konva-${page.id}`} key={page.id}>
              <CustomStage
                page={pageNow}
                totalPage={pages.length}
                strokeDraw={strokeDraw}
                tool={tool}
                roleStatus={roleStatus}
                setTool={setTool}
              />
            </div>
          ))}
        </Slider>
      </div>
    </div>
  )
})
