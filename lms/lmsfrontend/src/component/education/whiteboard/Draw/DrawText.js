import React, {useContext, useEffect, useRef, useState} from 'react'
import {Layer} from 'react-konva'
import {TransformerText} from './TransformerText'
import {updateLocalStorageData} from '../../../../utils/whiteboard/localStorage'
import {EVENT_TYPE, KEYS, SOCKET_IO_EVENTS, TOOL} from '../../../../utils/whiteboard/constants'
import {SocketContext} from '../../../../utils/whiteboard/context/SocketContext'
import {nanoid} from 'nanoid'

const LAYER_ID = 'text'

export const DrawText = React.memo(
  React.forwardRef(
    (
      {
        eventPointer,
        offset,
        tool,
        currentPage,
        stageContainer,
        onUpdateTool,
        whiteboardId,
        onDrawDone,
        totalPage,
        roleStatus,
      },
      ref,
    ) => {
      const { socket } = useContext(SocketContext)
      const [annotations, setAnnotations] = useState([])
      const [currentAnnotation, setCurrentAnnotation] = useState(null)
      const textRef = useRef({})

      const isInputtingRef = useRef('idle')
      const intervalRef = useRef(null)

      useEffect(() => {
        socket.on(SOCKET_IO_EVENTS.ON_ADD_TEXT_END, ({ data, isInputting, currentDrawPage }) => {
          if (Number(currentDrawPage) === Number(currentPage)) {
            // const textLayer = ref?.getLayers().find((layer) => layer.attrs.id === LAYER_ID)
            // if (
            //   (ref?.getLayers().length > 0 && textLayer?.getChildren().length / 2 !== data.length) ||
            //   !textLayer
            //   // annotations.length === data.length
            // ) {
            //   textLayer?.clear()
            //   textLayer?.destroyChildren()
            //   for (let i = 0; i < data.length; ++i) {
            //     const newText = new Konva.Text({
            //       x: data[i].x,
            //       y: data[i].y,
            //       width: data[i].width,
            //       draggable: tool !== TOOL.POINTER ? false : true,
            //       rotation: data[i].rotation,
            //       text: data[i].text,
            //       verticalAlign: 'bottom',
            //       fontSize: data[i].fontSize,
            //       fontFamily: data[i].fontFamily,
            //       fill: data[i].fill,
            //       padding: data[i].padding,
            //       align: data[i].align,
            //     })
            //     textRef.current[data[i].key] = newText
            //     // textRef.current[data[i].id].onClick = onClickText
            //     textLayer?.add(newText)

            //     const newTransform = new Konva.Transformer({
            //       boundBoxFunc: function (_, newBox) {
            //         newBox.width = Math.max(30, newBox.width)
            //         return newBox
            //       },
            //       rotation: 0,
            //       visible: false,
            //     })
            //     transformRef.current[data[i].key] = newTransform
            //     textLayer?.add(newTransform)
            //     newTransform.nodes([newText])

            //     newText.on('dblclick dbltap', () => onDblClickTextRect(newText, data[i]))
            //     newText.on('click', () => onClickText(newText, data[i]))
            //     newText.on('dragend', () => onDragTextEnd(newText, data[i]))
            //     newText.on('transformend', () => onTextTransformEnd(newText, data[i]))
            //   }
            //   textLayer?.batchDraw()
            // }

            // const newTransformRef = {}
            // for (const key of Object.keys(transformRef.current)) {
            //   if (transformRef.current.hasOwnProperty(key)) {
            //     if (textRef.current[key] !== null) {
            //       newTransformRef[key] = transformRef.current[key]
            //     }
            //   }
            // }
            // transformRef.current = newTransformRef
            // const newTextRef = {}
            // for (const key of Object.keys(textRef.current)) {
            //   if (textRef.current.hasOwnProperty(key)) {
            //     if (textRef.current[key] !== null) {
            //       newTextRef[key] = textRef.current[key]
            //     }
            //   }
            // }
            // textRef.current = newTextRef
            setAnnotations(data)
          }
          const drawData = JSON.parse(localStorage.getItem(KEYS.DRAW_DATA_LOCAL_STORAGE) || '{}')
          if (typeof drawData.text !== 'undefined') {
            drawData.text = updateLocalStorageData(drawData.text, data, currentPage)
          } else {
            drawData.text = [{ data, currentPage }]
          }
          localStorage.setItem(KEYS.DRAW_DATA_LOCAL_STORAGE, JSON.stringify(drawData))
          // isInputtingRef.current = isInputting
        })

        const onCheckLS = (currentWhiteboardId) => {
          if (whiteboardId !== currentWhiteboardId) {
            return
          }
          const drawData = JSON.parse(localStorage.getItem(KEYS.DRAW_DATA_LOCAL_STORAGE) || '{}')
          if (typeof drawData.text !== 'undefined') {
            const foundDrawData = drawData.text.find((item) => Number(item.currentPage) === Number(currentPage))
            if (typeof foundDrawData !== 'undefined') {
              setAnnotations(foundDrawData.data)
              // annotationsRef.current = foundDrawData.data
            } else {
              setAnnotations([])
              // annotationsRef.current = []
            }
          }
        }

        setTimeout(() => onCheckLS(), 150)

        socket.on(SOCKET_IO_EVENTS.ON_CHECK_LOCAL_STORAGE, ({ currentWhiteboardId }) => onCheckLS(currentWhiteboardId))

        return () => {
          socket.off(SOCKET_IO_EVENTS.ON_ADD_TEXT_END)
          // socket.off(SOCKET_IO_EVENTS.ON_CHECK_LOCAL_STORAGE)
        }
      }, [currentPage, annotations, totalPage])

      useEffect(() => {
        if (isInputtingRef.current !== 'processing') {
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
      }, [annotations])

      useEffect(() => {
        updateDataFromLS()
        // const id = setInterval(() => updateDataFromLS(), 2000)

        // return () => {
        //   clearInterval(id)
        // }
      }, [currentPage])

      useEffect(() => {
        if (eventPointer.eventType === null || eventPointer.eventType !== EVENT_TYPE.MOUSE_DOWN || tool !== TOOL.TEXT) {
          return
        }
        const { x, y } = eventPointer.pointerPosition
        const annotationToAdd = {
          x,
          y,
          text: 'Nhập gì đó...',
          padding: 5,
          fontSize: 16,
          fontFamily: 'Calibri',
          align: 'center',
          fill: '#000',
          width: 200,
          height: 100,
          rotation: 0,
          key: nanoid(),
          tool,
        }
        const newAnno = [...annotations, annotationToAdd]
        setAnnotations((prev) => [...prev, annotationToAdd])
        // annotationsRef.current = [...annotationsRef.current, annotationToAdd]
        const drawData = JSON.parse(localStorage.getItem(KEYS.DRAW_DATA_LOCAL_STORAGE) || '{}')
        if (typeof drawData.text !== 'undefined') {
          drawData.text = updateLocalStorageData(drawData.text, newAnno, currentPage)
        } else {
          drawData.text = [{ data: newAnno, currentPage }]
        }
        localStorage.setItem(KEYS.DRAW_DATA_LOCAL_STORAGE, JSON.stringify(drawData))
        socket.emit(SOCKET_IO_EVENTS.ADD_TEXT, {
          data: newAnno,
          currentDrawPage: currentPage,
          whiteboardPageId: `${whiteboardId}-${currentPage}`,
        })
        onDrawDone(TOOL.TEXT)
        onUpdateTool()
      }, [eventPointer, currentPage])

      useEffect(() => {
        if (!stageContainer && tool !== TOOL.ERASER) {
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
            if (typeof drawData.text !== 'undefined') {
              drawData.text = updateLocalStorageData(drawData.text, newAnnotations, currentPage)
            } else {
              drawData.text = [{ data: newAnnotations, currentPage }]
            }
            localStorage.setItem(KEYS.DRAW_DATA_LOCAL_STORAGE, JSON.stringify(drawData))
            socket.emit(SOCKET_IO_EVENTS.ADD_TEXT, {
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

      const onUpdateText = async (updatedAnnotation) => {
        const index = annotations.findIndex((ele) => ele.key === updatedAnnotation.key)
        const newAnnotationArray = [...annotations.slice(0, index), updatedAnnotation, ...annotations.slice(index + 1)]
        setAnnotations(newAnnotationArray)
        textRef.current[updatedAnnotation.key]?.text(updatedAnnotation.text)
        // annotationsRef.current = newAnnotationArray
        const drawData = JSON.parse(localStorage.getItem(KEYS.DRAW_DATA_LOCAL_STORAGE) || '{}')
        if (typeof drawData.text !== 'undefined') {
          drawData.text = updateLocalStorageData(drawData.text, newAnnotationArray, currentPage)
        } else {
          drawData.text = [{ data: newAnnotationArray, currentPage }]
        }
        localStorage.setItem(KEYS.DRAW_DATA_LOCAL_STORAGE, JSON.stringify(drawData))

        socket.emit(SOCKET_IO_EVENTS.ADD_TEXT, {
          data: newAnnotationArray,
          currentDrawPage: currentPage,
          isInputting: isInputtingRef.current,
          whiteboardPageId: `${whiteboardId}-${currentPage}`,
        })
      }

      const updateDataFromLS = () => {
        // if (isInputtingRef.current === 'processing') {
        //   return
        // }
        const drawData = JSON.parse(localStorage.getItem(KEYS.DRAW_DATA_LOCAL_STORAGE) || '{}')
        if (typeof drawData.text !== 'undefined') {
          const foundDrawData = drawData.text.find((item) => Number(item.currentPage) === Number(currentPage))
          if (typeof foundDrawData !== 'undefined') {
            setAnnotations(foundDrawData.data)
            // annotationsRef.current = foundDrawData.data
          } else {
            setAnnotations([])
            // annotationsRef.current = []
          }
        }
      }

      if (annotations.length === 0) {
        return null
      }

      return (
        <Layer id={LAYER_ID}>
          {annotations.map((value) => (
            <TransformerText
              key={value.key}
              ref={isInputtingRef}
              value={value}
              offset={offset}
              tool={tool}
              roleStatus={roleStatus}
              annotations={annotations}
              onUpdateText={onUpdateText}
              setCurrentAnnotation={setCurrentAnnotation}
            />
          ))}
        </Layer>
      )
    },
  ),
)
