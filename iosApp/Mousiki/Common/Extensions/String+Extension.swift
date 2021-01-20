//
//  String+Extension.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/20/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import Foundation
import UIKit

extension String {
    
    var localized: String {
        return Bundle.main.localizedString(forKey: self, value: nil, table: "Localizable")
    }
    
    func index(from: Int) -> Index {
        return self.index(startIndex, offsetBy: from)
    }
    
    func substring(from: Int) -> String {
        let fromIndex = index(from: from)
        return String(self[fromIndex...])
    }
    
    func deletingPrefix(_ prefix: String) -> String {
        guard self.hasPrefix(prefix) else { return self }
        return String(self.dropFirst(prefix.count))
    }
    
    func substring(to: Int) -> String {
        let toIndex = index(from: to)
        return String(self[..<toIndex])
    }
    
    func localized(with arguments: [CVarArg]) -> String {
        return String(format: localized, locale: nil, arguments: arguments)
    }
    
    func isValidEmail() -> Bool {
        let regex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}"
        return NSPredicate(format: "SELF MATCHES %@", regex).evaluate(with: self)
    }
    
    func isValidPhoneNumber() -> Bool {
        let regex = "0?\\d{9}"
        return NSPredicate(format: "SELF MATCHES %@", regex).evaluate(with: self)
    }
}

extension NSMutableAttributedString {
    
    func setClickableLink(text: String, link: String, underlineColor: UIColor = .colorPrimary) {
        let range = mutableString.range(of: text)
        addAttribute(NSAttributedString.Key.link, value: NSURL(string: link)!, range: range)
        addAttribute(NSAttributedString.Key.underlineStyle, value: NSNumber(value: 1), range: range)
        addAttribute(NSAttributedString.Key.underlineColor, value: underlineColor, range: range)
    }
}

