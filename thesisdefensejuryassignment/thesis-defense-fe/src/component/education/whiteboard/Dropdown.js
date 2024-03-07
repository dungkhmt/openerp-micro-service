import {Button, ClickAwayListener, Grow, MenuItem, MenuList, Paper, Popper} from '@material-ui/core'
import {makeStyles} from '@material-ui/core/styles'
import React from 'react'

const useStyles = makeStyles((theme) => ({
  root: {
    display: 'flex',
  },
  paper: {
    marginRight: theme.spacing(2),
  },
  menuItem: {
    display: 'flex',
    columnGap: 8,
  },
  popper: {
    zIndex: 100,
  },
}))

export const Dropdown = React.memo(({ pendingList, onApproveRequest, onRejectRequest }) => {
  const classes = useStyles()
  const [open, setOpen] = React.useState(false)
  const anchorRef = React.useRef(null)
  const prevOpen = React.useRef(open)

  // return focus to the button when we transitioned from !open -> open
  React.useEffect(() => {
    if (prevOpen.current === true && open === false) {
      anchorRef.current.focus()
    }

    prevOpen.current = open
  }, [open])

  const handleToggle = () => {
    setOpen((prevOpen) => !prevOpen)
  }

  const handleClose = (event) => {
    if (anchorRef.current && anchorRef.current.contains(event.target)) {
      return
    }

    setOpen(false)
  }

  function handleListKeyDown(event) {
    if (event.key === 'Tab') {
      event.preventDefault()
      setOpen(false)
    }
  }

  const onClickApproveRequestButton = (e, item) => {
    handleClose(e)
    onApproveRequest(item)
  }

  const onClickRejectRequestButton = (e, item) => {
    handleClose(e)
    onRejectRequest(item)
  }

  return (
    <div className={classes.root}>
      <div>
        <Button
          ref={anchorRef}
          aria-controls={open ? 'menu-list-grow' : undefined}
          aria-haspopup="true"
          onClick={handleToggle}
        >
          Danh sách yêu cầu
        </Button>
        <Popper
          className={classes.popper}
          open={open}
          anchorEl={anchorRef.current}
          role={undefined}
          transition
          disablePortal
        >
          {({ TransitionProps, placement }) => (
            <Grow
              {...TransitionProps}
              style={{ transformOrigin: placement === 'bottom' ? 'center top' : 'center bottom' }}
            >
              <Paper>
                <ClickAwayListener onClickAway={handleClose}>
                  <MenuList autoFocusItem={open} id="menu-list-grow" onKeyDown={handleListKeyDown}>
                    {pendingList.map((item) => (
                      <MenuItem key={item.userId} className={classes.menuItem}>
                        <p>{item.userId}</p>
                        <Button
                          variant="contained"
                          color="primary"
                          style={{ cursor: 'pointer', fontSize: '14px' }}
                          onClick={(e) => onClickApproveRequestButton(e, item)}
                        >
                          Chấp nhận
                        </Button>
                        <Button
                          variant="contained"
                          color="secondary"
                          style={{ cursor: 'pointer', fontSize: '14px' }}
                          onClick={(e) => onClickRejectRequestButton(e, item)}
                        >
                          Từ chối
                        </Button>
                      </MenuItem>
                    ))}
                  </MenuList>
                </ClickAwayListener>
              </Paper>
            </Grow>
          )}
        </Popper>
      </div>
    </div>
  )
})
