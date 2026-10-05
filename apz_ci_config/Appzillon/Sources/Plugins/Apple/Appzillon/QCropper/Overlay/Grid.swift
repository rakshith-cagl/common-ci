//
//  Grid.swift
//
import UIKit

class Grid: UIView {

    public var horizontalLinesCount: UInt = 2 {
        didSet {
            setNeedsDisplay()
        }
    }

    public var verticalLinesCount: UInt = 2 {
        didSet {
            setNeedsDisplay()
        }
    }

    public var lineColor: UIColor = UIColor(white: 1, alpha: 0.7) {
        didSet {
            setNeedsDisplay()
        }
    }

    public var lineWidth: CGFloat = 1.0 / UIScreen.main.scale {
        didSet {
            setNeedsDisplay()
        }
    }

    override func draw(_ rect: CGRect) {
        super.draw(rect)

        guard let context = UIGraphicsGetCurrentContext() else {
            return
        }

        context.setLineWidth(lineWidth)
        context.setStrokeColor(lineColor.cgColor)

        let horizontalLineSpacing = frame.size.width / CGFloat(horizontalLinesCount + 1)
        let verticalLineSpacing = frame.size.height / CGFloat(verticalLinesCount + 1)

        for index in 1 ..< horizontalLinesCount + 1 {
            context.move(to: CGPoint(x: CGFloat(index) * horizontalLineSpacing, y: 0))
            context.addLine(to: CGPoint(x: CGFloat(index) * horizontalLineSpacing, y: frame.size.height))
        }

        for index in 1 ..< verticalLinesCount + 1 {
            context.move(to: CGPoint(x: 0, y: CGFloat(index) * verticalLineSpacing))
            context.addLine(to: CGPoint(x: frame.size.width, y: CGFloat(index) * verticalLineSpacing))
        }
        context.strokePath()
    }
}
