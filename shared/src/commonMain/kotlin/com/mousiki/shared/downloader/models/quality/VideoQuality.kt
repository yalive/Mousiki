/*
 *  Copyright (c)  2021  Shabinder Singh
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.mousiki.shared.downloader.models.quality

enum class VideoQuality {
    unknown, highres,  // 3072p
    hd2880p, hd2160, hd1440, hd1080, hd720, large,  // 480p
    medium,  // 360p
    small,  // 240p
    tiny,  // 144p
    noVideo
}