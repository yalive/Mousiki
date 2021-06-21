//
//  SwiftYtExtractor.swift
//  Mousiki
//
//  Created by A.Yabahddou on 5/6/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import Foundation
import shared

class SwiftYtExtractor: DefaultExtractor {
        
    private let YT_PLAYER_CONFIG_PATTERNS = [
        ";ytplayer\\.config = (\\{.*?\\})\\;ytplayer",
        ";ytplayer\\.config = (\\{.*?\\})\\;",
        "ytInitialPlayerResponse\\s*=\\s*(\\{.+?\\})\\;var meta"
    ]
    
    override func extractYtPlayerConfig(html: String) throws -> String {
        for pattern in YT_PLAYER_CONFIG_PATTERNS {
            if let json = matches(for: pattern, in: html) {
                return json
            }
        }
        return ""
    }
    
    func matches(for regex: String, in text: String) -> String? {
        
        do {
            let regex = try NSRegularExpression(pattern: regex)
            let matches = regex.matches(in: text, options: [], range: NSRange(location: 0, length: text.utf16.count))
            
            if let match = matches.first {
                let range = match.range(at:1)
                if let swiftRange = Range(range, in: text) {
                   return String(text[swiftRange])
                }
            }
        } catch {
        }
        
        return nil
    }
}
