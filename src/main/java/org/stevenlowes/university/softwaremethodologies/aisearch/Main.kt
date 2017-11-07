package org.stevenlowes.university.softwaremethodologies.aisearch

import org.stevenlowes.university.softwaremethodologies.aisearch.input.TextParser
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Matrix
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.groupers.SimpleGrouper
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers.AntColonySolver

fun main2(args: Array<String>) {
    val rootLevel = TextParser.parseFile("testfiles/10.txt")
    //val solver = SimulatedAnnealingSolver(70.0, 1.0, 1.5, 1000, 1000000)
    val solver = AntColonySolver(5000, 2.0, 2.0, 0.6f, 1f)
    val grouper = SimpleGrouper()
    //val grouper = EnergeticGrouper(3f, 15)
    val matrix = Matrix(solver, grouper)
    matrix.setRootLevel(rootLevel)
    val startTime = System.nanoTime()
    matrix.createAllLevels()
    val path = matrix.solve()
    val endTime = System.nanoTime()
    println(path)
    println(path.distance)
    println("Done!")
    val dif = (endTime - startTime).toDouble() / 1000 / 1000 / 1000
    println(dif)
}

fun main3(args: Array<String>) {
    val rootLevel = TextParser.parseFile("testfiles/1.txt")
    val array = rootLevel.array
    (0..(array.size - 1)).forEach { y ->
        val min = y * array.size
        val max = (y + 1) * array.size
        val subarray = array.array.toList().subList(min, max)
        println(subarray)
    }
}

fun main1(args: Array<String>) {
    val list = "449, 328, 207, 216, 341, 483, 343, 65, 302, 404, 162, 463, 503, 268, 212, 145, 395, 2, 391, 490, 64, 163, 266, 360, 231, 436, 253, 96, 20, 170, 51, 11, 192, 233, 461, 124, 322, 359, 447, 505, 412, 477, 474, 484, 517, 534, 283, 210, 316, 321, 282, 406, 158, 452, 107, 88, 146, 262, 451, 10, 407, 308, 257, 344, 516, 202, 384, 445, 528, 525, 527, 277, 115, 524, 309, 405, 43, 307, 399, 430, 248, 446, 346, 339, 291, 223, 414, 38, 273, 352, 285, 377, 242, 396, 108, 485, 431, 363, 476, 348, 303, 195, 472, 371, 368, 378, 150, 453, 481, 57, 209, 468, 221, 419, 182, 286, 49, 82, 189, 120, 83, 422, 254, 136, 218, 81, 74, 270, 443, 129, 462, 420, 21, 103, 98, 132, 258, 48, 492, 238, 119, 197, 345, 327, 434, 184, 104, 45, 427, 425, 501, 506, 458, 502, 204, 263, 111, 125, 366, 226, 418, 188, 22, 400, 121, 457, 365, 112, 382, 432, 71, 53, 261, 259, 369, 300, 151, 260, 531, 487, 87, 513, 533, 40, 421, 433, 127, 75, 330, 109, 441, 274, 335, 272, 423, 497, 62, 141, 63, 54, 515, 310, 169, 297, 117, 256, 219, 428, 296, 237, 23, 172, 392, 211, 251, 304, 389, 278, 196, 224, 319, 243, 398, 161, 439, 361, 97, 122, 201, 171, 526, 498, 518, 486, 130, 61, 313, 55, 187, 12, 403, 495, 69, 469, 459, 269, 198, 91, 299, 167, 417, 437, 271, 118, 401, 134, 203, 347, 456, 380, 0, 72, 157, 519, 228, 159, 92, 499, 448, 355, 331, 193, 372, 337, 28, 168, 318, 465, 144, 397, 383, 312, 206, 489, 47, 225, 482, 306, 409, 289, 46, 232, 324, 56, 76, 138, 110, 426, 512, 471, 27, 480, 267, 287, 329, 504, 185, 529, 19, 148, 6, 394, 522, 408, 326, 354, 166, 33, 336, 155, 227, 292, 332, 280, 473, 90, 140, 116, 73, 8, 131, 18, 177, 153, 357, 106, 521, 68, 175, 78, 149, 455, 264, 34, 381, 42, 79, 315, 99, 255, 77, 217, 246, 105, 507, 35, 475, 126, 241, 356, 450, 353, 424, 222, 3, 173, 333, 388, 114, 143, 139, 137, 147, 493, 60, 113, 435, 275, 85, 351, 390, 101, 37, 29, 50, 281, 393, 230, 25, 250, 180, 31, 67, 32, 279, 135, 234, 294, 59, 460, 496, 367, 133, 284, 288, 323, 338, 440, 4, 220, 178, 293, 199, 478, 364, 376, 444, 467, 214, 334, 413, 84, 95, 252, 358, 58, 265, 190, 470, 466, 276, 17, 179, 100, 52, 387, 240, 142, 342, 245, 415, 523, 500, 15, 80, 1, 239, 290, 375, 374, 508, 30, 44, 249, 349, 298, 402, 311, 385, 181, 205, 429, 373, 160, 5, 156, 70, 454, 13, 410, 350, 176, 154, 191, 510, 9, 208, 494, 416, 320, 174, 236, 379, 165, 314, 479, 128, 66, 530, 14, 41, 93, 94, 491, 386, 102, 215, 164, 442, 235, 244, 200, 247, 362, 36, 295, 194, 26, 86, 229, 16, 305, 152, 123, 514, 370, 488, 532, 340, 317, 89, 7, 39, 24, 186, 509, 438, 411, 301, 511, 520, 464, 213, 325, 183".replace(
            Regex(" "),
            "").split(",").map { it.toInt() }.toIntArray()
    println(list.size)
    val alreadySeen = mutableListOf<Int>()
    list.forEach {
        if (it in alreadySeen) {
            println("Duplicate $it")
        }
        alreadySeen.add(it)
    }

    val rootLevel = TextParser.parseFile("testfiles/10.txt")
    println(rootLevel.array.getDistance(list))
}

fun main(args: Array<String>) {
    main2(args)
}