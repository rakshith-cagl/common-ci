//
//  SignaturePad.swift
//  Appzillon
//
//  Created by Bhavya V on 06/07/21.
//
// swiftlint:disable all
import Foundation
import UIKit

@objc
public protocol SignaturePadDelegate: AnyObject {
    func didStart()
    func didFinish()
    @available(*, unavailable, renamed: "didFinish()")
    func startedDrawing()
    @available(*, unavailable, renamed: "didFinish()")
    func finishedDrawing()
}

open class SignaturePad: UIView {
    private var path: UIBezierPath = UIBezierPath()
    private var dot: UIBezierPath = UIBezierPath()
    private var incrementalImage: UIImage?
    private var points: [CGPoint] = [CGPoint](repeating: CGPoint(), count: 5)
    private var ctr: Int = 0
    private var strokeColor: UIColor = UIColor.black
    private var lineWidth: CGFloat = 2.0
    weak var delegate: SignaturePadDelegate?
    open var isSigned: Bool {
        get {
            guard let _ = incrementalImage else {
                return false
            }
            return true
        }
    }
    // Only override draw() if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override open func draw(_ rect: CGRect) {
        // Drawing code
        incrementalImage?.draw(in: rect)
        strokeColor.setStroke()
        path.stroke()
    }
    required public init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        self.isMultipleTouchEnabled = false
        self.strokeColor.setStroke()
        path = UIBezierPath()
        path.lineWidth = lineWidth
    }
    public override init(frame: CGRect) {
        super.init(frame: frame)
        self.isMultipleTouchEnabled = false
        self.strokeColor.setStroke()
        path = UIBezierPath()
        path.lineWidth = lineWidth
    }
    override open func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let touch = touches.first {
            ctr = 0
            points[0] = touch.location(in: self)
        }
        if let delegate = delegate {
            delegate.didStart()
        }
    }
    override open func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let touch = touches.first {
            let point: CGPoint = touch.location(in: self)
            ctr += 1
            points[ctr] = point
            if ctr == 4 {
                points[3] = CGPoint(x: (points[2].x + points[4].x)/2.0, y: (points[2].y + points[4].y)/2.0)
                path.move(to: points[0])
                path.addCurve(to: points[3], controlPoint1: points[1], controlPoint2: points[2])
                self.setNeedsDisplay()
                points[0] = points[3]
                points[1] = points[4]
                ctr = 1
            }
        }
    }
    override open func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        if ctr < 4 {
            switch ctr {
            case 0:
                dot = UIBezierPath(ovalIn: CGRect(x: points[0].x, y: points[0].y,
                                                  width: lineWidth/2, height: lineWidth/2))
            case 1:
                dot = UIBezierPath(ovalIn: CGRect(x: points[0].x, y: points[0].y,
                                                  width: lineWidth/2, height: lineWidth/2))
                points[0] = points[1]
            case 2:
                dot = UIBezierPath(ovalIn: CGRect(x: points[0].x, y: points[0].y,
                                                  width: lineWidth/2, height: lineWidth/2))
                points[0] = points[2]
            case 3:
                path.move(to: points[0])
                path.addCurve(to: points[3], controlPoint1: points[1], controlPoint2: points[2])
                points[0] = points[3]
            default:
                break
            }
        } else {
            points[0] = path.currentPoint
        }
        self.drawBitmap()
        self.setNeedsDisplay()
        path.removeAllPoints()
        dot.removeAllPoints()
        ctr = 0
        if let delegate = delegate {
            delegate.didFinish()
        }
    }
    override open func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.touchesEnded(touches, with: event)
    }
    func drawBitmap() {
        UIGraphicsBeginImageContextWithOptions(self.bounds.size, true, 0.0)
        if incrementalImage == nil {
            let rectPath = UIBezierPath(rect: self.bounds)
            UIColor.white.setFill()
            rectPath.fill()
        }
        incrementalImage?.draw(at: CGPoint.zero)
        self.strokeColor.setStroke()
        self.strokeColor.setFill()
        path.stroke()
        dot.fill()
        incrementalImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
    }
    open func clear() {
        incrementalImage = nil
        dot = UIBezierPath()
        path = UIBezierPath()
        path.lineWidth = lineWidth
        self.setNeedsDisplay()
    }
    open func setSignature(_ image: UIImage) {
        if incrementalImage == nil {
            let rectPath = UIBezierPath(rect: self.bounds)
            UIColor.white.setFill()
            rectPath.fill()
        }
        UIGraphicsBeginImageContext(self.bounds.size)
        image.draw(in: CGRect(x: 0, y: 0, width: self.bounds.size.width, height: self.bounds.size.height))
        incrementalImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        self.setNeedsDisplay()
    }
    open func getSignature() -> UIImage? {
        UIGraphicsBeginImageContextWithOptions(self.bounds.size, self.isOpaque, 0.0)
        self.drawHierarchy(in: self.bounds, afterScreenUpdates: false)
        let signature = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return signature
    }
}
// swiftlint:enable all
