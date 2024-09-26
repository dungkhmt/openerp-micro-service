import React, {useContext, useEffect, useRef, useState} from 'react'
import {Layer, Stage} from 'react-konva'
import {drawLines} from '../../../../utils/whiteboard/drawGrid'
import {EVENT_TYPE, SOCKET_IO_EVENTS, TOOL} from '../../../../utils/whiteboard/constants'
import {DrawCircle} from '../Draw/DrawCircle'
import {DrawLine} from '../Draw/DrawLine'
import {DrawRectangle} from '../Draw/DrawRectangle'
import {DrawText} from '../Draw/DrawText'
import {useParams} from 'react-router'
import {SocketContext} from 'utils/whiteboard/context/SocketContext'
import {useWindowSize} from 'utils/whiteboard/hooks/useWindowSize'

const scaleBy = 1.05
const MAX_SCALE = 3.125
const MIN_SCALE = 0.25

export const CustomStage = React.memo(({ page, totalPage, strokeDraw, tool, roleStatus, setTool }) => {
  const { whiteboardId } = useParams()
  const { socket } = useContext(SocketContext)
  const { width } = useWindowSize()

  const [scale, setScale] = useState(1)
  const [eventPointer, setEventPointer] = useState({
    [TOOL.PEN]: { eventType: null, pointerPosition: { x: 0, y: 0 } },
    [TOOL.RECTANGLE]: { eventType: null, pointerPosition: { x: 0, y: 0 } },
    [TOOL.CIRCLE]: { eventType: null, pointerPosition: { x: 0, y: 0 } },
    [TOOL.TEXT]: { eventType: null, pointerPosition: { x: 0, y: 0 } },
  })
  const [offset, setOffset] = useState()
  const [stageContainer, setStageContainer] = useState(null)

  const stageRef = useRef(null)
  const isDrawing = useRef(false)
  const gridLayerRef = useRef(null)

  useEffect(() => {
    if (stageRef && stageRef.current) {
      setStageContainer(stageRef.current?.container())
    }
    socket.emit(SOCKET_IO_EVENTS.CONNECT_TO_WHITEBOARD_PAGE, {
      whiteboardPageId: `${whiteboardId}-${page}`,
    })
  }, [page])

  useEffect(() => {
    setEventPointer({
      [TOOL.PEN]: { eventType: null, pointerPosition: { x: 0, y: 0 } },
      [TOOL.RECTANGLE]: { eventType: null, pointerPosition: { x: 0, y: 0 } },
      [TOOL.CIRCLE]: { eventType: null, pointerPosition: { x: 0, y: 0 } },
      [TOOL.TEXT]: { eventType: null, pointerPosition: { x: 0, y: 0 } },
    })
  }, [tool, page])

  useEffect(() => {
    if (gridLayerRef && gridLayerRef.current && stageRef.current) {
      // drawLines(gridLayerRef, stageRef.current[Number(queryString.get('page')) - 1], width, height)
      drawLines(gridLayerRef, stageRef.current, 1200, 595)
    }
  }, [totalPage, scale, page, width])

  const handleMouseDown = (e) => {
    if (tool !== TOOL.TEXT && tool !== TOOL.POINTER) {
      e.evt.preventDefault()
      e.evt.stopPropagation()
    }
    isDrawing.current = true
    let pos = e.target.getStage()?.getPointerPosition()
    if (scale !== 1) {
      pos = e.currentTarget.getRelativePointerPosition()
    }
    if (tool === TOOL.ERASER) {
      setEventPointer((prev) => ({
        ...prev,
        [TOOL.PEN]: {
          eventType: EVENT_TYPE.MOUSE_DOWN,
          pointerPosition: { x: pos?.x, y: pos?.y },
        },
      }))
    } else {
      setEventPointer((prev) => ({
        ...prev,
        [tool]: {
          eventType: EVENT_TYPE.MOUSE_DOWN,
          pointerPosition: { x: pos?.x, y: pos?.y },
        },
      }))
    }
    setOffset({
      // top: stageRef.current[Number(queryString.get('page')) - 1]?.container().offsetTop,
      // left: stageRef.current[Number(queryString.get('page')) - 1]?.container().offsetLeft,
      top: stageRef.current[page - 1]?.container().offsetTop,
      left: stageRef.current[page - 1]?.container().offsetLeft,
    })
  }

  const handleMouseMove = (e) => {
    if (tool !== TOOL.TEXT && tool !== TOOL.POINTER) {
      e.evt.preventDefault()
      e.evt.stopPropagation()
    }
    // no drawing - skipping
    if (!isDrawing.current) {
      return
    }
    const stage = e.target.getStage()
    let point = stage?.getPointerPosition()
    if (scale !== 1) {
      point = e.currentTarget.getRelativePointerPosition()
    }
    if (tool === TOOL.ERASER) {
      setEventPointer((prev) => ({
        ...prev,
        [TOOL.PEN]: {
          eventType: EVENT_TYPE.MOUSE_MOVE,
          pointerPosition: { x: point?.x, y: point?.y },
        },
      }))
    } else {
      setEventPointer((prev) => ({
        ...prev,
        [tool]: {
          eventType: EVENT_TYPE.MOUSE_MOVE,
          pointerPosition: { x: point?.x, y: point?.y },
        },
      }))
    }
  }

  const handleMouseUp = (e) => {
    if (tool !== TOOL.TEXT && tool !== TOOL.POINTER) {
      e.evt.preventDefault()
      e.evt.stopPropagation()
    }
    isDrawing.current = false
    let point = e.target.getStage()?.getPointerPosition()
    if (scale !== 1) {
      point = e.currentTarget.getRelativePointerPosition()
    }
    if (tool === TOOL.ERASER) {
      setEventPointer((prev) => ({
        ...prev,
        [TOOL.PEN]: {
          eventType: EVENT_TYPE.MOUSE_UP,
          pointerPosition: { x: point?.x, y: point?.y },
        },
      }))
    } else {
      setEventPointer((prev) => ({
        ...prev,
        [tool]: {
          eventType: EVENT_TYPE.MOUSE_UP,
          pointerPosition: { x: point?.x, y: point?.y },
        },
      }))
    }
  }

  const handleWheel = (e) => {
    if (tool !== TOOL.TEXT && tool !== TOOL.POINTER) {
      e.evt.preventDefault()
      e.evt.stopPropagation()
    }
    const stage = e.target.getStage()
    // const layer = e.target.g()

    const oldScale = stage?.scaleX()
    const pointer = stage?.getPointerPosition()
    const mousePointTo = {
      x: (pointer?.x - Number(stage?.x())) / Number(oldScale),
      y: (pointer?.y - Number(stage?.y())) / Number(oldScale),
    }
    // check is zoom in or zoom out
    let direction = e.evt.deltaY > 0 ? 1 : -1
    // when we zoom on trackpad, e.evt.ctrlKey is true
    // in that case lets revert direction
    if (e.evt.ctrlKey) {
      direction = -direction
    }

    const newScale = direction > 0 ? Number(oldScale) * scaleBy : Number(oldScale) / scaleBy
    if (newScale < MIN_SCALE || newScale > MAX_SCALE) {
      return
    }
    stage?.scale({ x: newScale, y: newScale })
    setScale(newScale)
    const newPos = {
      x: pointer?.x - mousePointTo.x * newScale,
      y: pointer?.y - mousePointTo.y * newScale,
    }
    stage?.position(newPos)

    drawLines(gridLayerRef, stage, 1200, 595)
  }

  const onSetDefaultTool = () => setTool(TOOL.POINTER)

  const onDrawDone = (tool) => {
    setEventPointer((prev) => ({
      ...prev,
      [tool]: { eventType: null, pointerPosition: { x: 0, y: 0 } },
    }))
  }

  const onTouchStart = (e) => {
    if (tool !== TOOL.TEXT && tool !== TOOL.POINTER) {
      e.evt.preventDefault()
      e.evt.stopPropagation()
    }
    handleMouseDown(e)
  }

  const onTouchMove = (e) => {
    if (tool !== TOOL.TEXT && tool !== TOOL.POINTER) {
      e.evt.preventDefault()
      e.evt.stopPropagation()
    }
    handleMouseMove(e)
  }

  const onTouchEnd = (e) => {
    if (tool !== TOOL.TEXT && tool !== TOOL.POINTER) {
      e.evt.preventDefault()
      e.evt.stopPropagation()
    }
    handleMouseUp(e)
  }

  return (
    <Stage
      ref={stageRef}
      width={1200}
      height={595}
      onMouseDown={handleMouseDown}
      onMousemove={handleMouseMove}
      onMouseup={handleMouseUp}
      onTouchStart={onTouchStart}
      onTouchMove={onTouchMove}
      onTouchEnd={onTouchEnd}
      onWheel={handleWheel}
    >
      <Layer id="grid" ref={gridLayerRef} draggable={false} x={0} y={0} />
      <DrawLine
        eventPointer={eventPointer[TOOL.PEN]}
        scale={scale}
        tool={tool}
        currentPage={page}
        whiteboardId={whiteboardId}
        totalPage={totalPage}
        strokeDraw={strokeDraw}
        onDrawDone={onDrawDone}
        ref={stageRef.current}
      />
      <DrawRectangle
        eventPointer={eventPointer[TOOL.RECTANGLE]}
        scale={scale}
        tool={tool}
        currentPage={page}
        whiteboardId={whiteboardId}
        stageContainer={stageContainer}
        totalPage={totalPage}
        strokeDraw={strokeDraw}
        onDrawDone={onDrawDone}
        roleStatus={roleStatus}
        ref={stageRef.current}
      />
      <DrawCircle
        eventPointer={eventPointer[TOOL.CIRCLE]}
        scale={scale}
        tool={tool}
        currentPage={page}
        whiteboardId={whiteboardId}
        stageContainer={stageContainer}
        onDrawDone={onDrawDone}
        strokeDraw={strokeDraw}
        totalPage={totalPage}
        roleStatus={roleStatus}
        ref={stageRef.current}
      />
      <DrawText
        eventPointer={eventPointer[TOOL.TEXT]}
        offset={offset}
        tool={tool}
        currentPage={page}
        stageContainer={stageContainer}
        onUpdateTool={onSetDefaultTool}
        whiteboardId={whiteboardId}
        onDrawDone={onDrawDone}
        totalPage={totalPage}
        roleStatus={roleStatus}
        ref={stageRef.current}
      />
    </Stage>
  )
})
