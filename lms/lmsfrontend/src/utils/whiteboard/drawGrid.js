import Konva from "konva"

const stepSize = 40

function unScale(val, scaleNumber) {
  return val / scaleNumber
}

export function drawLines(
  gridLayerRef,
  stage,
  width,
  height,
) {
  gridLayerRef.current?.clear()
  gridLayerRef.current?.destroyChildren()
  gridLayerRef.current?.clipWidth(0) // clear any clipping

  const stageRect = {
    x1: 0,
    y1: 0,
    x2: stage?.width(),
    y2: stage?.height(),
    offset: {
      x: unScale(stage?.position().x, stage?.scaleX()),
      y: unScale(stage?.position().y, stage?.scaleX()),
    },
  }
  const viewRect = {
    x1: -stageRect.offset.x,
    y1: -stageRect.offset.y,
    x2: unScale(width, stage?.scaleX()) - stageRect.offset.x,
    y2: unScale(height, stage?.scaleX()) - stageRect.offset.y,
  }

  const gridOffset = {
    x: Math.ceil(unScale(stage?.position().x, stage?.scaleX()) / stepSize) * stepSize,
    y: Math.ceil(unScale(stage?.position().y, stage?.scaleX()) / stepSize) * stepSize,
  }
  const gridRect = {
    x1: -gridOffset.x,
    y1: -gridOffset.y,
    x2: unScale(width, stage?.scaleX()) - gridOffset.x + stepSize,
    y2: unScale(height, stage?.scaleX()) - gridOffset.y + stepSize,
  }
  const gridFullRect = {
    x1: Math.min(stageRect.x1, gridRect.x1),
    y1: Math.min(stageRect.y1, gridRect.y1),
    x2: Math.max(stageRect?.x2, gridRect.x2),
    y2: Math.max(stageRect?.y2, gridRect.y2),
  }

  // set clip function to stop leaking lines into non-viewable space.
  gridLayerRef.current?.clip({
    x: viewRect.x1,
    y: viewRect.y1,
    width: viewRect.x2 - viewRect.x1,
    height: viewRect.y2 - viewRect.y1,
  })

  const // find the x & y size of the grid
    xSize = gridFullRect.x2 - gridFullRect.x1,
    ySize = gridFullRect.y2 - gridFullRect.y1,
    // compute the number of steps required on each axis.
    xSteps = Math.round(xSize / stepSize),
    ySteps = Math.round(ySize / stepSize)

  // draw vertical lines
  for (let i = 0; i <= xSteps; i++) {
    gridLayerRef.current?.add(
      new Konva.Line({
        x: gridFullRect.x1 + i * stepSize,
        y: gridFullRect.y1,
        points: [0, 0, 0, ySize],
        stroke: 'rgba(0, 0, 0, 0.2)',
        strokeWidth: 1,
      }),
    )
  }
  //draw Horizontal lines
  for (let i = 0; i <= ySteps; i++) {
    gridLayerRef.current?.add(
      new Konva.Line({
        x: gridFullRect.x1,
        y: gridFullRect.y1 + i * stepSize,
        points: [0, 0, xSize, 0],
        stroke: 'rgba(0, 0, 0, 0.2)',
        strokeWidth: 1,
      }),
    )
  }

  gridLayerRef.current?.batchDraw()
}
