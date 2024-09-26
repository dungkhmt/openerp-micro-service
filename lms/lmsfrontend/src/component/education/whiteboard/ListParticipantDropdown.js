import ClickAwayListener from '@material-ui/core/ClickAwayListener'
import Grow from '@material-ui/core/Grow'
import Paper from '@material-ui/core/Paper'
import Popper from '@material-ui/core/Popper'
import MenuItem from '@material-ui/core/MenuItem'
import MenuList from '@material-ui/core/MenuList'
import {makeStyles} from '@material-ui/core/styles'
import Button from '@material-ui/core/Button'
import React from 'react'
import {ROLE_STATUS} from 'utils/whiteboard/constants'

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

export const ListParticipantDropdown = React.memo(({ list, onRejectRequest }) => {
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

  const onClickRemoveButton = (e, item) => {
    handleClose(e)
    onRejectRequest(item)
  }

  if (list.length === 0) {
    return null
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
          Danh sách người tham gia
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
                    {list.map((item) => (
                      <MenuItem key={item.userId} className={classes.menuItem}>
                        <div>{item.userId}</div>
                        {!item.isCreatedUser &&
                          item.roleId === ROLE_STATUS.WRITE &&
                          item.statusId === ROLE_STATUS.ACCEPTED && (
                            <Button
                              variant="contained"
                              color="secondary"
                              style={{ cursor: 'pointer' }}
                              onClick={(e) => onClickRemoveButton(e, item)}
                            >
                              Hùy quyền vẽ
                            </Button>
                          )}
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
