import React, {useContext, useEffect, useRef, useState} from 'react'
import {Group, Layer, Line} from 'react-konva'
import {updateLocalStorageData} from '../../../../utils/whiteboard/localStorage'
import {EVENT_TYPE, KEYS, SOCKET_IO_EVENTS, TOOL} from '../../../../utils/whiteboard/constants'
import {SocketContext} from '../../../../utils/whiteboard/context/SocketContext'
import {nanoid} from 'nanoid'

const LAYER_ID = 'line'

export const DrawLine = React.memo(
  React.forwardRef(
    ({ eventPointer, scale, tool, currentPage, whiteboardId, onDrawDone, totalPage, strokeDraw }, ref) => {
      const { socket } = useContext(SocketContext)
      const [lines, setLines] = useState([])

      const isDrawingRef = useRef(false)
      const intervalRef = useRef(null)

      useEffect(() => {
        socket.on(SOCKET_IO_EVENTS.ON_DRAW_LINE_END, ({ data, currentDrawPage }) => {
          if (currentDrawPage === Number(currentPage)) {
            setLines(data)
            // const lineLayer = ref?.getLayers().find((layer) => layer.attrs.id === LAYER_ID)
            // // layerRef.current?.batchDraw()
            // if ((ref?.getLayers().length > 0 && lineLayer?.getChildren().length !== data.length) || !lineLayer) {
            //   lineLayer?.clear()
            //   lineLayer?.destroyChildren()
            //   for (let i = 0; i < data.length; ++i) {
            //     lineLayer?.add(
            //       new Konva.Line({
            //         points: data[i].points,
            //         stroke: data[i].strokeDraw.color,
            //         strokeWidth: data[i].strokeWidth * scale,
            //         tension: 0.5,
            //         lineCap: 'round',
            //         lineJoin: 'round',
            //         globalCompositeOperation: data[i].tool === TOOL.ERASER ? 'destination-out' : 'source-over',
            //       }),
            //     )
            //   }
            //   lineLayer?.batchDraw()
            // lineLayer?.draw()
            // ref?.getLayer()?.batchDraw()
            // }
            // linesRef.current = data
          }
          const drawData = JSON.parse(localStorage.getItem(KEYS.DRAW_DATA_LOCAL_STORAGE) || '{}')
          if (typeof drawData.lines !== 'undefined') {
            drawData.lines = updateLocalStorageData(drawData.lines, data, currentPage)
          } else {
            drawData.lines = [{ data, currentPage }]
          }
          localStorage.setItem(KEYS.DRAW_DATA_LOCAL_STORAGE, JSON.stringify(drawData))
        })

        const onCheckLS = (currentWhiteboardId) => {
          if (whiteboardId !== currentWhiteboardId) {
            return
          }
          updateDataFromLS()
        }

        // onCheckLS()

        socket.on(SOCKET_IO_EVENTS.ON_CHECK_LOCAL_STORAGE, ({ currentWhiteboardId }) => onCheckLS(currentWhiteboardId))

        return () => {
          socket.off(SOCKET_IO_EVENTS.ON_DRAW_LINE_END)
          // socket.off(SOCKET_IO_EVENTS.ON_CHECK_LOCAL_STORAGE)
        }
      }, [currentPage, totalPage])

      useEffect(() => {
        if (!isDrawingRef.current) {
          if (intervalRef && intervalRef.current) {
            clearInterval(intervalRef.current)
          }
          intervalRef.current = setInterval(() => updateDataFromLS(), 2000)
        } else {
          if (intervalRef && intervalRef.current) {
            clearInterval(intervalRef.current)
          }
        }

        return () => {
          if (intervalRef && intervalRef.current) {
            clearInterval(intervalRef.current)
          }
        }
      }, [lines])

      useEffect(() => {
        updateDataFromLS()
      }, [currentPage])

      useEffect(() => {
        if (eventPointer.eventType === null || (tool !== TOOL.PEN && tool !== TOOL.ERASER)) {
          return
        }

        if (eventPointer.eventType === EVENT_TYPE.MOUSE_DOWN) {
          isDrawingRef.current = true
          const { x, y } = eventPointer.pointerPosition
          // linesRef.current = [...linesRef.current, { tool, points: [x, y] }]
          setLines([
            ...lines,
            { tool, points: [x, y], strokeDraw: strokeDraw.color, strokeWidth: strokeDraw.strokeWidth, key: nanoid() },
          ])
        } else if (eventPointer.eventType === EVENT_TYPE.MOUSE_MOVE) {
          if (!isDrawingRef.current) {
            return
          }
          const { x, y } = eventPointer.pointerPosition
          const lastLine = lines[lines.length - 1]
          // add point
          lastLine.points = [...lastLine.points, x, y]
          // socket.emit('draw', { x: point?.x, y: point?.y })

          // replace last
          const newLines = JSON.parse(JSON.stringify(lines))
          newLines.splice(lines.length - 1, 1, lastLine)
          // linesRef.current = [...linesRef.current.slice(0, linesRef.current.length - 1), lastLine]
          setLines(newLines)
        } else if (eventPointer.eventType === EVENT_TYPE.MOUSE_UP) {
          const drawData = JSON.parse(localStorage.getItem(KEYS.DRAW_DATA_LOCAL_STORAGE) || '{}')
          if (typeof drawData.lines !== 'undefined') {
            drawData.lines = updateLocalStorageData(drawData.lines, lines, currentPage)
          } else {
            drawData.lines = [{ data: lines, currentPage }]
          }
          localStorage.setItem(KEYS.DRAW_DATA_LOCAL_STORAGE, JSON.stringify(drawData))
          socket.emit(SOCKET_IO_EVENTS.DRAW_LINE_END, {
            data: lines,
            currentDrawPage: Number(currentPage),
            whiteboardPageId: `${whiteboardId}-${currentPage}`,
          })
          onDrawDone(TOOL.PEN)
          isDrawingRef.current = false
        }
      }, [eventPointer, currentPage, strokeDraw])

      const updateDataFromLS = () => {
        const drawData = JSON.parse(localStorage.getItem(KEYS.DRAW_DATA_LOCAL_STORAGE) || '{}')
        if (typeof drawData.lines !== 'undefined') {
          const foundDrawData = drawData.lines.find((item) => Number(item.currentPage) === Number(currentPage))
          if (typeof foundDrawData !== 'undefined') {
            setLines(foundDrawData.data)
            // linesRef.current = foundDrawData.data
          } else {
            setLines([])
            // linesRef.current = []
          }
        }
      }

      if (lines.length === 0) {
        return null
      }

      return (
        <Layer id={LAYER_ID}>
          <Group>
            {lines.map((line, i) => (
              <Line
                key={line.key}
                points={line.points}
                stroke={line.strokeDraw}
                strokeWidth={line.strokeWidth * scale}
                tension={0.5}
                lineCap="round"
                lineJoin="round"
                globalCompositeOperation={line.tool === TOOL.ERASER ? 'destination-out' : 'source-over'}
              />
            ))}
          </Group>
        </Layer>
      )
    },
  ),
)
