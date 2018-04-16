package ktorrent.protocol.metainfo

import ktorrent.bencoding.*
import ktorrent.protocol.MappingException
import java.io.OutputStream

data class FileInfo(val length: Long,
                    val path: List<String>,
                    val md5Sum: String?,
                    val original: BDictionary = BDictionary()) : BEncodable {

    override fun write(outputStream: OutputStream) {
        val dictionary = BDictionary(original)
        dictionary += mapOf(
                "length" to BInteger(length),
                "path" to BList(path.map { BByteString(it) })
        )
        md5Sum?.let { dictionary["md5sum"] = BByteString(it) }
        dictionary.write(outputStream)
    }

    companion object {

        internal fun read(dictionary: BDictionary) = FileInfo(
                length = (dictionary["length"] as? BInteger)?.value
                        ?: throw MappingException("Failed to read file length"),
                path = (dictionary["path"] as? BList)?.map {
                    (it as? BByteString)?.string
                            ?: throw MappingException("Failed to read file path")
                } ?: throw MappingException("Failed to read file path"),
                md5Sum = (dictionary["md5sum"] as? BByteString)?.string,
                original = dictionary
        )
    }
}
