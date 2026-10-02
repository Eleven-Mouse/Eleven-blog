// 友链卡片解析：<!-- friends --> 标记后的 "- [名称](链接 "头像URL")" 列表。
// 头像可省略，展示组件（FriendWall）会自动读取站点 favicon，失败时降级为名称首字占位。

export const FRIENDS_MARKER_PATTERN = /<!--\s*friends\s*-->/
export const FRIEND_LIST_ITEM_PATTERN = /^-\s+\[([^\]]+)\]\(\s*([^)\s]+)\s*(?:\s+"([^"]*)")?\)\s*$/

export const parseFriendItem = (line) => {
  const match = line.match(FRIEND_LIST_ITEM_PATTERN)
  if (!match) return null
  return {
    name: match[1].trim(),
    url: match[2].trim(),
    avatar: (match[3] || '').trim(),
  }
}

// 把友链列表整体替换为挂载占位符（由 FriendWall 组件填充），返回新原文与解析结果。
export const extractFriendLinks = (source) => {
  const lines = String(source || '').split('\n')
  const markerIndex = lines.findIndex((line) => FRIENDS_MARKER_PATTERN.test(line))
  if (markerIndex < 0) return null

  const friends = []
  let lastIndex = markerIndex
  for (let i = markerIndex + 1; i < lines.length; i += 1) {
    const line = lines[i].trim()
    if (!line) continue
    const friend = parseFriendItem(line)
    if (!friend) break
    friends.push(friend)
    lastIndex = i
  }
  if (!friends.length) return null

  lines.splice(markerIndex, lastIndex - markerIndex + 1, '<div data-friend-wall="0"></div>')
  return { source: lines.join('\n'), friends }
}
