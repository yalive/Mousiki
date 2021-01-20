//
//  UIViewController+Network.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/20/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit
import SVProgressHUD

protocol Networkable: class {
    func showLoading()
    func hideLoading()
    func showError(_ message: String)
    func cancelRequest()
}

extension Networkable where Self: UIViewController {
    func showLoading() {
        if !SVProgressHUD.isVisible() {
            SVProgressHUD.setForegroundColor(.colorPrimary)
            SVProgressHUD.setDefaultMaskType(.black)
            SVProgressHUD.show()
        }
    }

    func hideLoading() {
        SVProgressHUD.dismiss()
    }

    func showError(_ message: String) {
        showAlert(message: message)
    }

    func cancelRequest() {
        URLSession.shared.invalidateAndCancel()
    }
}
