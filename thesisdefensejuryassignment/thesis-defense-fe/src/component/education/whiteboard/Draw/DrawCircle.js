import React, {useContext, useEffect, useState} from 'react'
import {Circle, Layer} from 'react-konva'
import {updateLocalStorageData} from '../../../../utils/whiteboard/localStorage'
import {EVENT_TYPE, KEYS, ROLE_STATUS, SOCKET_IO_EVENTS, TOOL} from '../../../../utils/whiteboard/constants'
import {SocketContext} from '../../../../utils/whiteboard/context/SocketContext'
import Konva from 'konva'
import {nanoid} from 'nanoid'

const LAYER_ID = 'circle'

export const DrawCircle = React.memo(
  React.forwardRef(
    (
      {
        eventPointer,
        scale,
        tool,
        currentPage,
        stageContainer,
        whiteboardId,
        onDrawDone,
        totalPage,
        strokeDraw,
        roleStatus,
      },
      ref,
    ) => {
      const { socket } = useContext(SocketContext)
      const [annotations, setAnnotations] = useState([])
      const [newAnnotation, setNewAnnotation] = useState([])
      const [currentAnnotation, setCurrentAnnotation] = useState(null)

      useEffect(() => {
        socket.on(SOCKET_IO_EVENTS.ON_DRAW_CIRCLE_END, ({ data, currentDrawPage }) => {
          if (Number(currentPage) === Number(currentDrawPage)) {
            const circleLayer = ref?.getLayers().find((layer) => layer.attrs.id === LAYER_ID)
            setAnnotations(data)
            if (ref?.getLayers().length > 0 && circleLayer?.getChildren().length !== data.length) {
              circleLayer?.clear()
              circleLayer?.destroyChildren()
              for (let i = 0; i < data.length; ++i) {
                circleLayer?.add(
                  new Konva.Circle({
                    x: data[i].x,
                    y: data[i].y,
                    radius: data[i].radius,
                    stroke: currentAnnotation !== null && currentAnnotation.key === data[i].key ? 'red' : data[i].color,
                    strokeWidth: data[i].strokeWidth * scale,
                    tension: 0.5,
                    lineCap: 'round',
                    lineJoin: 'round',
                    globalCompositeOperation: 'source-over',
                    onClick: () => onClickCircle(data[i].key),
                  }),
                )
              }
              circleLayer?.batchDraw()
            }
            // annotationsRef.current = data
          }
          const drawData = JSON.parse(localStorage.getItem(KEYS.DRAW_DATA_LOCAL_STORAGE) || '{}')
          if (typeof drawData.circle !== 'undefined') {
            drawData.circle = updateLocalStorageData(drawData.circle, data, currentPage)
          } else {
            drawData.circle = [{ data, currentPage }]
          }
          localStorage.setItem(KEYS.DRAW_DATA_LOCAL_STORAGE, JSON.stringify(drawData))
        })

        const onCheckLS = (currentWhiteboardId) => {
          if (whiteboardId !== currentWhiteboardId) {
            return
          }
          updateDataFromLS()
        }

        setTimeout(() => onCheckLS(), 150)

        socket.on(SOCKET_IO_EVENTS.ON_CHECK_LOCAL_STORAGE, ({ currentWhiteboardId }) => onCheckLS(currentWhiteboardId))

        return () => {
          socket.off(SOCKET_IO_EVENTS.ON_DRAW_CIRCLE_END)
          // socket.off(SOCKET_IO_EVENTS.ON_CHECK_LOCAL_STORAGE)
        }
      }, [currentPage, totalPage])

      useEffect(() => {
        updateDataFromLS()
        const id = setInterval(() => updateDataFromLS(), 2000)

        return () => {
          clearInterval(id)
        }
      }, [currentPage])

      useEffect(() => {
        if (eventPointer.eventType === null || (tool !== TOOL.CIRCLE && tool !== TOOL.ERASER)) {
          return
        }
        if (eventPointer.eventType === EVENT_TYPE.MOUSE_DOWN) {
          if (newAnnotation.length === 0) {
            const { x, y } = eventPointer.pointerPosition
            setNewAnnotation([
              { x, y, radius: 0, key: nanoid(), tool, color: strokeDraw.color, strokeWidth: strokeDraw.strokeWidth },
            ])
          }
        } else if (eventPointer.eventType === EVENT_TYPE.MOUSE_MOVE) {
          if (newAnnotation.length === 1) {
            const { x, y } = eventPointer.pointerPosition
            setNewAnnotation([
              {
                x: newAnnotation[0].x,
                y: newAnnotation[0].y,
                radius: Math.sqrt(Math.pow(x - newAnnotation[0].x, 2) + Math.pow(y - newAnnotation[0].y, 2)),
                key: nanoid(),
                tool,
                color: strokeDraw.color,
                strokeWidth: strokeDraw.strokeWidth,
              },
            ])
          }
        } else if (eventPointer.eventType === EVENT_TYPE.MOUSE_UP) {
          if (newAnnotation.length === 1) {
            const { x, y } = eventPointer.pointerPosition
            const annotationToAdd = {
              x: newAnnotation[0].x,
              y: newAnnotation[0].y,
              radius: Math.sqrt(Math.pow(x - newAnnotation[0].x, 2) + Math.pow(y - newAnnotation[0].y, 2)),
              key: nanoid(),
              tool,
              color: strokeDraw.color,
              strokeWidth: strokeDraw.strokeWidth,
            }
            setNewAnnotation([])
            const newAnno = [...annotations, annotationToAdd]
            setAnnotations((prev) => [...prev, annotationToAdd])
            // annotationsRef.current = [...annotationsRef.current, annotationToAdd]
            const drawData = JSON.parse(localStorage.getItem(KEYS.DRAW_DATA_LOCAL_STORAGE) || '{}')
            if (typeof drawData.circle !== 'undefined') {
              drawData.circle = updateLocalStorageData(drawData.circle, newAnno, currentPage)
            } else {
              drawData.circle = [{ data: newAnno, currentPage }]
            }
            localStorage.setItem(KEYS.DRAW_DATA_LOCAL_STORAGE, JSON.stringify(drawData))
            socket.emit(SOCKET_IO_EVENTS.DRAW_CIRCLE_END, {
              data: newAnno,
              currentDrawPage: Number(currentPage),
              whiteboardPageId: `${whiteboardId}-${currentPage}`,
            })
            onDrawDone(TOOL.CIRCLE)
          }
        }
      }, [eventPointer, currentPage])

      useEffect(() => {
        if (!stageContainer) {
          return
        }
        stageContainer.tabIndex = 1
        stageContainer.focus()

        const listener = (e) => {
          if (e.key === 'Delete' && currentAnnotation !== null) {
            const newAnnotations = annotations.filter((annotation) => annotation.key !== currentAnnotation.key)
            setAnnotations(newAnnotations)
            setCurrentAnnotation(null)
            // annotationsRef.current = newAnnotations

            const drawData = JSON.parse(localStorage.getItem(KEYS.DRAW_DATA_LOCAL_STORAGE) || '{}')
            if (typeof drawData.circle !== 'undefined') {
              drawData.circle = updateLocalStorageData(drawData.circle, newAnnotations, currentPage)
            } else {
              drawData.circle = [{ data: newAnnotations, currentPage }]
            }
            localStorage.setItem(KEYS.DRAW_DATA_LOCAL_STORAGE, JSON.stringify(drawData))
            socket.emit(SOCKET_IO_EVENTS.DRAW_CIRCLE_END, {
              data: newAnnotations,
              currentDrawPage: Number(currentPage),
              whiteboardPageId: `${whiteboardId}-${currentPage}`,
            })
          }
        }

        stageContainer.addEventListener('keydown', listener)

        return () => {
          stageContainer.removeEventListener('keydown', listener)
        }
      }, [stageContainer, currentAnnotation, tool])

      const onClickCircle = (key) => {
        if (roleStatus.roleId === ROLE_STATUS.READ || roleStatus.statusId !== ROLE_STATUS.ACCEPTED) {
          return
        }
        setCurrentAnnotation(annotations.find((item) => item.key === key))
      }

      const updateDataFromLS = () => {
        const drawData = JSON.parse(localStorage.getItem(KEYS.DRAW_DATA_LOCAL_STORAGE) || '{}')
        if (typeof drawData.circle !== 'undefined') {
          const foundDrawData = drawData.circle.find((item) => Number(item.currentPage) === Number(currentPage))
          if (typeof foundDrawData !== 'undefined') {
            setAnnotations(foundDrawData.data)
            // annotationsRef.current = foundDrawData.data
          } else {
            setAnnotations([])
            // annotationsRef.current = []
          }
        }
      }

      const annotationsToDraw = [...annotations, ...newAnnotation]

      if (annotationsToDraw.length === 0) {
        return null
      }

      return (
        <Layer id={LAYER_ID}>
          {annotationsToDraw.map((value) => (
            <Circle
              key={value.key}
              x={value.x}
              y={value.y}
              radius={value.radius}
              stroke={currentAnnotation !== null && currentAnnotation.key === value.key ? 'red' : value.color}
              strokeWidth={value.strokeWidth * scale}
              tension={0.5}
              lineCap="round"
              lineJoin="round"
              onClick={() => onClickCircle(value.key)}
              globalCompositeOperation="source-over"
            />
          ))}
        </Layer>
      )
    },
  ),
)
