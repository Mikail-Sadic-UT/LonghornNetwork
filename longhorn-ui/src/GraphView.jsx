import { useRef, useEffect, useState, useCallback } from 'react'

// Draws the student connection graph on a canvas
// Nodes in a circle, weighted edges between them, hover to highlight
export default function GraphView({ students, edges }) {
    const canvasRef = useRef(null)
    const [hovered, setHovered] = useState(null)  // which node is being hovered
    const nodeRadius = 28
    const padding = 60
    const displaySize = 540

    // Calculate x,y for each student in a circle layout
    const getPositions = useCallback((size) => {
        const cx = size / 2
        const cy = size / 2
        const radius = cx - padding
        const positions = {}
        students.forEach((s, i) => {
            const angle = (2 * Math.PI * i) / students.length - Math.PI / 2
            positions[s.name] = {
                x: cx + radius * Math.cos(angle),
                y: cy + radius * Math.sin(angle),
            }
        })
        return positions
    }, [students])

    // Redraw whenever data or hover changes
    useEffect(() => {
        const canvas = canvasRef.current
        if (!canvas) return
        const ctx = canvas.getContext('2d')
        const dpr = window.devicePixelRatio || 1

        // Scale up the pixel buffer for sharp rendering on hidpi
        canvas.width = displaySize * dpr
        canvas.height = displaySize * dpr
        ctx.scale(dpr, dpr)

        const pos = getPositions(displaySize)
        ctx.clearRect(0, 0, displaySize, displaySize)

        // Draw edges first (so nodes sit on top)
        edges.forEach(e => {
            const from = pos[e.source]
            const to = pos[e.target]
            if (!from || !to) return

            const isHl = hovered && (e.source === hovered || e.target === hovered)

            // Line
            ctx.beginPath()
            ctx.moveTo(from.x, from.y)
            ctx.lineTo(to.x, to.y)
            ctx.strokeStyle = isHl ? '#bf5700' : '#3a3a5c'
            ctx.lineWidth = isHl ? 2.5 : 1.5
            ctx.stroke()

            // Weight label at midpoint
            const mx = (from.x + to.x) / 2
            const my = (from.y + to.y) / 2
            const text = String(e.weight)
            ctx.font = 'bold 13px sans-serif'
            ctx.textAlign = 'center'
            ctx.textBaseline = 'middle'
            const tw = ctx.measureText(text).width + 8
            ctx.fillStyle = '#111128'                     // bg pill
            ctx.fillRect(mx - tw / 2, my - 9, tw, 18)
            ctx.fillStyle = isHl ? '#ff8c3a' : '#888'    // text color
            ctx.fillText(text, mx, my)
        })

        // Draw nodes
        students.forEach(s => {
            const p = pos[s.name]
            const isHov = s.name === hovered

            ctx.beginPath()
            ctx.arc(p.x, p.y, nodeRadius, 0, 2 * Math.PI)
            ctx.fillStyle = isHov ? '#bf5700' : '#16213e'
            ctx.fill()
            ctx.strokeStyle = isHov ? '#ff8c3a' : '#bf5700'
            ctx.lineWidth = 2
            ctx.stroke()

            // Name inside the circle
            ctx.fillStyle = '#fff'
            ctx.font = 'bold 13px sans-serif'
            ctx.textAlign = 'center'
            ctx.textBaseline = 'middle'
            ctx.fillText(s.name, p.x, p.y)
        })
    }, [students, edges, hovered, getPositions])

    // Check if mouse is over a node
    const handleMouseMove = (e) => {
        const canvas = canvasRef.current
        const rect = canvas.getBoundingClientRect()
        // Convert screen coords to canvas coords
        const mx = (e.clientX - rect.left) * (displaySize / rect.width)
        const my = (e.clientY - rect.top) * (displaySize / rect.height)
        const pos = getPositions(displaySize)

        let found = null
        for (const s of students) {
            const p = pos[s.name]
            const dx = mx - p.x
            const dy = my - p.y
            if (dx * dx + dy * dy <= nodeRadius * nodeRadius) {
                found = s.name
                break
            }
        }
        setHovered(found)
    }

    return (
        <canvas
            ref={canvasRef}
            style={{
                width: displaySize,
                height: displaySize,
                background: '#111128',
                borderRadius: 12,
                display: 'block',
            }}
            onMouseMove={handleMouseMove}
            onMouseLeave={() => setHovered(null)}
        />
    )
}
