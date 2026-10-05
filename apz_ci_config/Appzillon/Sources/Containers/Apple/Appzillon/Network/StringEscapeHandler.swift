//
//  StringEscapeHandler.swift
//  Appzillon
//
//  Created by manjunath.ramesh on 02/03/22.
//

import Foundation

extension Unicode.Scalar {
    var hexa: String { .init(value, radix: 16, uppercase: true) }
}

extension Character {
    var hexaValues: [String] {
        unicodeScalars
            .map(\.hexa)
            .map { #"\u"# + repeatElement("0", count: 4-$0.count) + $0 }
    }
}

extension StringProtocol where Self: RangeReplaceableCollection {
    var asciiRepresentation: String { map { $0.isASCII ? .init($0) : $0.hexaValues.joined() }.joined() }
}

 class StringEscapeHandler: NSObject {
    @objc static func escapeJava(string: String) -> String {
//        print(string.asciiRepresentation)
        return string.asciiRepresentation
    }
}
