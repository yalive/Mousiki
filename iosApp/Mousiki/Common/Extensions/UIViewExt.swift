//
//  UIViewExt.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/20/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit

extension UIView {
    
    var parentViewController: UIViewController? {
        var parentResponder: UIResponder? = self
        while parentResponder != nil {
            parentResponder = parentResponder?.next
            if let viewController = parentResponder as? UIViewController {
                return viewController
            }
        }
        return nil
    }
    
    func setCorner(radius: CGFloat) {
        layer.cornerRadius = radius
        clipsToBounds = true
    }
    
    func setBorder(width: CGFloat, color: UIColor) {
        layer.borderColor = color.cgColor
        layer.borderWidth = width
    }
    
    func roundCorners(_ corners: UIRectCorner, radius: CGFloat) {
        let path = UIBezierPath(roundedRect: self.bounds, byRoundingCorners: corners, cornerRadii: CGSize(width: radius, height: radius))
        let mask = CAShapeLayer()
        mask.path = path.cgPath
        self.layer.mask = mask
    }
    
    func addShadow(color: UIColor = .colorPrimary, radius: Float = 0.5, shadowOpacity: Float = 1) {
        self.layer.shadowColor = color.cgColor
        self.layer.shadowOpacity = shadowOpacity
        self.layer.shadowOffset = .zero
        self.layer.shadowRadius = CGFloat(radius)
    }
    
    func showAnimation(_ completionBlock: @escaping () -> Void) {
        isUserInteractionEnabled = false
        UIView.animate(withDuration: 0.1,
                       delay: 0,
                       options: .curveLinear,
                       animations: { [weak self] in
                        self?.transform = CGAffineTransform.init(scaleX: 0.95, y: 0.95)
                       }) {  (done) in
            UIView.animate(withDuration: 0.1,
                           delay: 0,
                           options: .curveLinear,
                           animations: { [weak self] in
                            self?.transform = CGAffineTransform.init(scaleX: 1, y: 1)
                           }) { [weak self] (_) in
                self?.isUserInteractionEnabled = true
                completionBlock()
            }
                       }
    }
}
