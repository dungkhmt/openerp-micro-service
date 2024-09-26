import {Button, Tooltip} from '@material-ui/core'
import MaterialTable from 'material-table'
import React, {useEffect, useState} from 'react'
import {Link, useHistory, useParams, useRouteMatch} from 'react-router-dom'
import AddIcon from '@material-ui/icons/Add'
import RemoveIcon from '@material-ui/icons/Remove'
import {request} from '../../../api'
import {nanoid} from 'nanoid'
import {KEYS, ROLE_STATUS} from '../../../utils/whiteboard/constants'
import {toast} from 'react-toastify'
import TextField from '@material-ui/core/TextField'
import Dialog from '@material-ui/core/Dialog'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogContentText from '@material-ui/core/DialogContentText'
import DialogTitle from '@material-ui/core/DialogTitle'

export default function ListWhiteBoard() {
  const { url } = useRouteMatch()
  const { sessionId } = useParams()
  const history = useHistory()
  const [listWhiteboard, setListWhiteboard] = useState([])
  const [openSetNameModal, setOpenSetNameModal] = useState(false)
  const [openDeleteWhiteboardModal, setOpenDeleteWhiteboardModal] = React.useState(false)
  const [whiteboardName, setWhiteboardName] = useState('')
  const [deleteWhiteboardId, setDeleteWhiteboardId] = useState('')

  const columns = [
    {
      title: 'WhiteboardId',
      field: 'id',
      render: (rowData) => <Link to={`${url}/whiteboard/${rowData['id']}?page=1`}>{rowData['id']}</Link>,
    },
    { title: 'Tên', field: 'name', render: (rowData) => <p>{rowData['name'] || `Whiteboard ${rowData['id']}`}</p> },
    { title: 'Tổng số trang', field: 'page', render: (rowData) => <p>{rowData['totalPage']}</p> },
    { title: 'Người tạo', field: 'createdUser', render: (rowData) => <p>{rowData['createdBy']}</p> },
    {
      title: 'Hành động',
      field: 'action',
      render: (rowData) => (
        <Tooltip title="Xóa bảng viết" aria-label="Xóa bảng viết" placement="top">
          <Button variant="contained" color="secondary" onClick={() => handleOpenDeleteWhiteboardModal(rowData['id'])}>
            <RemoveIcon style={{ color: 'white' }} fontSize="default" />
            &nbsp;&nbsp;&nbsp;Xóa&nbsp;&nbsp;
          </Button>
        </Tooltip>
      ),
    },
  ]

  const getListWhiteboard = async () => {
    await request(
      'get',
      `/whiteboards/${sessionId}`,
      (res) => {
        setListWhiteboard(res.data)
      },
      {},
      {},
    )
  }

  useEffect(() => {
    void getListWhiteboard()
  }, [])

  const onCreateNewWhiteboard = async () => {
    if (!whiteboardName) {
      toast.error('Bạn cần nhập tên cho bảng viết để tiếp tục', {
        position: toast.POSITION.BOTTOM_RIGHT,
      })
      return
    }
    const whiteboardId = nanoid()
    // create new whiteboard
    localStorage.removeItem(KEYS.DRAW_DATA_LOCAL_STORAGE)
    localStorage.removeItem(KEYS.TOTAL_PAGE)
    localStorage.removeItem(KEYS.CURRENT_PAGE)
    await request(
      'post',
      '/whiteboards',
      async () => {
        await request(
          'put',
          `/whiteboards/user/${whiteboardId}`,
          (res) => {
            if (res.status === 200) {
              setWhiteboardName('')
              localStorage.setItem(KEYS.USER_ID, res?.data?.userId)
            }
            history.push(`${url}/whiteboard/${whiteboardId}?page=1`)
          },
          {},
          { roleId: ROLE_STATUS.WRITE, statusId: ROLE_STATUS.ACCEPTED },
        )
      },
      {},
      { whiteboardId, classSessionId: sessionId, whiteboardName },
    )
  }

  const onDeleteWhiteboard = async () => {
    await request(
      'delete',
      '/whiteboards',
      async (res) => {
        if (res?.data?.success) {
          await getListWhiteboard()
          toast.success(`Xoá bảng viết ${deleteWhiteboardId} thành công.`, {
            position: toast.POSITION.BOTTOM_RIGHT,
          })
        } else {
          toast.error(res.data?.message ?? 'Có lỗi xảy ra', {
            position: toast.POSITION.BOTTOM_RIGHT,
          })
        }
        setOpenDeleteWhiteboardModal(false)
        setDeleteWhiteboardId('')
      },
      {},
      { whiteboardId: deleteWhiteboardId, classSessionId: sessionId },
    )
  }

  const handleOpenSetNameModal = () => {
    setOpenSetNameModal(true)
  }

  const handleCloseSetNameModal = () => {
    setOpenSetNameModal(false)
    setWhiteboardName('')
  }

  const handleOpenDeleteWhiteboardModal = (whiteboardId) => {
    setDeleteWhiteboardId(whiteboardId)
    setOpenDeleteWhiteboardModal(true)
  }

  const handleCloseDeleteWhiteboardModal = () => {
    setOpenDeleteWhiteboardModal(false)
    setDeleteWhiteboardId('')
  }

  return (
    <div>
      <MaterialTable
        title="Whiteboard List"
        columns={columns}
        data={listWhiteboard}
        localization={{
          header: {
            actions: '',
          },
          body: {
            emptyDataSourceMessage: 'Không có bản ghi nào để hiển thị',
            filterRow: {
              filterTooltip: 'Lọc',
            },
          },
        }}
        options={{
          search: true,
          sorting: false,
          actionsColumnIndex: -1,
          pageSize: 8,
          tableLayout: 'fixed',
        }}
        style={{
          fontSize: 14,
        }}
        actions={[
          {
            icon: () => (
              <Tooltip title="Thêm bảng viết" aria-label="Thêm bảng viết" placement="top">
                <Button variant="contained" color="primary" onClick={handleOpenSetNameModal}>
                  <AddIcon style={{ color: 'white' }} fontSize="default" />
                  &nbsp;&nbsp;&nbsp;Thêm mới&nbsp;&nbsp;
                </Button>
              </Tooltip>
            ),
            isFreeAction: true,
          },
        ]}
      />
      <Dialog
        open={openSetNameModal}
        onClose={handleCloseSetNameModal}
        aria-labelledby="form-dialog-create-whiteboard-name"
      >
        <DialogTitle id="form-dialog-create-whiteboard-name">Nhập tên bảng viết</DialogTitle>
        <DialogContent>
          <DialogContentText>Để tiếp tục, bạn cần nhập tên cho bảng viết.</DialogContentText>
          <TextField
            autoFocus
            margin="dense"
            id="name"
            label="Tên bảng viết"
            type="text"
            fullWidth
            value={whiteboardName}
            onChange={(e) => setWhiteboardName(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseSetNameModal} color="secondary">
            Hủy
          </Button>
          <Button onClick={onCreateNewWhiteboard} color="primary">
            Tạo
          </Button>
        </DialogActions>
      </Dialog>
      <Dialog
        open={openDeleteWhiteboardModal}
        onClose={handleCloseDeleteWhiteboardModal}
        aria-labelledby="alert-dialog-delete-whiteboard"
        aria-describedby="alert-dialog-delete-whiteboard-desc"
      >
        <DialogTitle id="alert-dialog-delete-whiteboard">
          Bạn muốn xóa bảng viết có id {deleteWhiteboardId} không?
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-delete-whiteboard-desc">
            Khi đã xóa, bảng viết không thể khôi phục được.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDeleteWhiteboardModal} color="primary">
            Hủy
          </Button>
          <Button onClick={onDeleteWhiteboard} color="secondary" autoFocus>
            Xóa
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  )
}
