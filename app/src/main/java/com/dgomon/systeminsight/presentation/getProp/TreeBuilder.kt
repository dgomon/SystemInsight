package com.dgomon.systeminsight.presentation.getProp

class TreeBuilder {
    fun buildTree(lines: List<String>): TreeNode {
        val root = TreeNode("root")

        for (line in lines) {
            val (rawKey, rawValue) = line.split("]: [")
            val key = rawKey.removePrefix("[").trim()
            val value = rawValue.removeSuffix("]").trim()
            val parts = key.split(".")

            var current = root
            for (i in parts.indices) {
                val part = parts[i]
                val existing = current.children.find { it.name == part }
                if (existing != null) {
                    current = existing
                } else {
                    val newNode = TreeNode(name = part)
                    current.children.add(newNode)
                    current = newNode
                }
            }
            current.value = value
        }

        return root
    }
}