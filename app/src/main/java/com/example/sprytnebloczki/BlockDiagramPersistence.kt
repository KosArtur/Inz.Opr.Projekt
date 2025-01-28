package com.example.sprytnebloczki

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class BlockDiagramPersistence {

    private val gson = Gson()

    // Zapisuje diagram blokowy do pliku
    fun saveToFile(context: Context, blocks: List<Block>, uri: Uri) {

        try {
            Log.d("save","test2")
            // Uzyskujemy OutputStream dla przekazanego URI
            val outputStream = context.contentResolver.openOutputStream(uri)
            Log.d("save","test3")
            outputStream?.use { stream ->
                // Mapujemy bloki na obiekty BlockWithConnections
                val blocksWithConnections = blocks.map { block ->

                    //val lineTrue = (block as? IfBlock)?.getLineTrue()
                    //val lineFalse = (block as? IfBlock)?.getLineFalse()

                    BlockWithConnections(
                        id = block.getId(),
                        type = block.getType(),
                        connectedBlocks = listOfNotNull(block.getPreviousBlock()?.getId(), block.getNextBlock()?.getId()),
                        //connectedBlocks = block.getLine().map { it.getConnectedBlock()?.getId() },
                        firstValue = (block as? OperationBlock)?.getFirstValue(),
                        secondValue = (block as? OperationBlock)?.getSecondValue(),
                        thirdValue = (block as? OperationBlock)?.getThirdValue(),
                        action = block.getAction(),
                        InputAction = (block as? InputBlock)?.getAction(),
                        values = (block as? InputBlock)?.getUserInput(),
                        //inputType = (block as? InputBlock)?.getInputType(),
                        ifFirstValue = (block as? IfBlock)?.getFirstValue(),
                        ifSecondValue = (block as? IfBlock)?.getSecondValue(),
                        ifAction = (block as? IfBlock)?.getAction(),
                        //lineTrueId = lineTrue?.getId(),
                        //lineFalseId = lineFalse?.getId(),
                        blockTrue = (block as? IfBlock)?.getBlockTrue()?.getId(),
                        blockFalse = (block as? IfBlock)?.getBlockFalse()?.getId()
                    )
                }

                // Tworzymy strukturę JSON zgodną z poprzednią wersją
                val json = gson.toJson(blocksWithConnections)

                // Zapisujemy dane do OutputStream
                stream.write(json.toByteArray())
                Toast.makeText(context, "Plik zapisany pomyślnie", Toast.LENGTH_SHORT).show()

                outputStream.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("save","$e")
            Toast.makeText(context, "Błąd zapisywania pliku", Toast.LENGTH_LONG).show()
        }
    }

    // Odczytuje diagram blokowy z pliku
    fun loadFromFile(uri: Uri, context: Context): Pair<List<Block>, MutableMap<Block, List<Block>>>? {
        return try {
            // Otwieramy plik za pomocą ContentResolver
            val fileContent = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().use { reader -> reader.readText() }
            }

            // Jeśli plik jest pusty lub nie udało się go odczytać
            if (fileContent.isNullOrEmpty()) {
                Log.e("BlockDiagramPersistence", "Plik jest pusty lub nie można go odczytać.")
                return null
            }

            // Logowanie zawartości pliku
            Log.d("BlockDiagramPersistence", "Zawartość pliku: $fileContent")

            // Parsowanie JSON i mapowanie na obiekty
            val blocksWithConnections: Array<BlockWithConnections> =
                gson.fromJson(fileContent, Array<BlockWithConnections>::class.java)

            Log.d("BlockDiagramPersistence", "Odczytano ${blocksWithConnections.size} bloków z pliku.")
            blocksWithConnections.forEach { blockData ->
                Log.d("BlockDiagramPersistence", "Blok ID: ${blockData.id}, Połączone bloki: ${blockData.connectedBlocks}")
            }

            // Mapowanie odczytanych danych na obiekty bloków
            /*
            val blocks = blocksWithConnections.map { data ->
                val block = createBlockFromData(data, context)
                Log.d("BlockDiagramPersistence", "Utworzono blok: ${block.getId()} z danymi: $data")
                block
            }

            // Przywracanie połączeń między blokami
            val blockMap = blocks.associateBy { it.getId() } // Mapujemy bloki po ID
            val connectionsMap = mutableMapOf<Block, List<Block>>()


            blocksWithConnections.forEach { data ->
                val block = blockMap[data.id] // Znajdź blok w mapie po ID
                val connectedBlocks = data.connectedBlocks.mapNotNull { id -> blockMap[id] }
                Log.d("BlockDiagramPersistence", "Blok ${data.id} ma ${connectedBlocks.size} połączeń.")
                block?.let { connectionsMap[it] = connectedBlocks }

                // Ustawiamy połączenia blockTrue i blockFalse po stworzeniu wszystkich bloków
                if (block is IfBlock) {
                    data.blockTrue?.let { trueBlockId ->
                        block.setBlockTrue(blockMap[trueBlockId])
                    }
                    data.blockFalse?.let { falseBlockId ->
                        block.setBlockFalse(blockMap[falseBlockId])
                    }
                }
            }
            */
            // Mapowanie odczytanych danych na obiekty bloków
            val fileIdToBlock = mutableMapOf<Int, Block>() // Mapa: ID z pliku -> Obiekt bloku
            val blocks = blocksWithConnections.map { data ->
                val block = createBlockFromData(data, context)
                fileIdToBlock[data.id] = block // Przypisanie ID z pliku do nowego bloku
                Log.d("BlockDiagramPersistence", "Utworzono blok: ${block.getId()} z danymi: $data")
                block
            }

            // Mapa bloków według aktualnych ID (nowe ID)
            val blockMap = blocks.associateBy { it.getId() } // Mapujemy bloki po ich nowych ID

            // Przywracanie połączeń między blokami
            val connectionsMap = mutableMapOf<Block, List<Block>>()

            blocksWithConnections.forEach { data ->
                val block = fileIdToBlock[data.id] // Znajdź blok w mapie po ID z pliku
                val connectedBlocks = data.connectedBlocks.mapNotNull { id -> fileIdToBlock[id] } // Połączenia po ID z pliku
                Log.d("BlockDiagramPersistence", "Blok ${data.id} ma ${connectedBlocks.size} połączeń.")
                block?.let { connectionsMap[it] = connectedBlocks }

                // Ustawiamy połączenia blockTrue i blockFalse po stworzeniu wszystkich bloków
                if (block is IfBlock) {
                    data.blockTrue?.let { trueBlockId ->
                        block.setBlockTrue(fileIdToBlock[trueBlockId]) // Używamy mapy ID z pliku
                    }
                    data.blockFalse?.let { falseBlockId ->
                        block.setBlockFalse(fileIdToBlock[falseBlockId]) // Używamy mapy ID z pliku
                    }
                }
            }

            // Zwracamy bloki i mapę połączeń
            Pair(blocks, connectionsMap)
        } catch (e: Exception) {
            e.printStackTrace()
            null // W przypadku błędu zwracamy null
        }
    }

    // Funkcja tworząca blok na podstawie danych (zależnie od typu)
    private fun createBlockFromData(data: BlockWithConnections, context: Context): Block {
        Log.d("FileContent", "Typ bloku: ${data.type}")
        return when (data.type) {
            "operacja" -> {
                val block = OperationBlock(
                    image = FrameLayout(context), // Tworzymy widok bloku
                    type = data.type
                )
                block.setFirstValue(data.firstValue ?: "")  // Ustawiamy wartości
                block.setSecondValue(data.secondValue ?: "")
                block.setThirdValue(data.thirdValue ?: "") // Ustawiamy thirdValue
                block.setAction(data.action ?: "")
                block.connectedLines = mutableListOf() // Inicjalizujemy listę połączeń
                block
            }
            "warunek" -> {
                val block = IfBlock(
                    image = FrameLayout(context), // Tworzymy widok bloku
                    type = data.type
                )
                block.setFirstValue(data.ifFirstValue ?: "")
                block.setSecondValue(data.ifSecondValue ?: "")
                block.setAction(data.ifAction ?: "")
                block.connectedLines = mutableListOf() // Inicjalizujemy listę połączeń
                block
            }
            "input" -> {
                val block = InputBlock(
                    image = FrameLayout(context), // Tworzymy widok bloku
                    type = data.type,
                    action = data.action ?: "",
                    values = data.values ?: ""
                )
                //block.setInputType(data.inputType ?: "" )
                block.connectedLines = mutableListOf() // Inicjalizujemy listę połączeń
                block
            }
            "start", "koniec" -> {
                val block = Block(
                    image = FrameLayout(context), // Tworzymy widok bloku
                    type = data.type
                )
                block.connectedLines = mutableListOf() // Inicjalizujemy listę połączeń
                block
            }
            else -> throw IllegalArgumentException("Nieznany typ bloku")
        }
    }

}



// Klasa pomocnicza do zapisywania danych o blokach i ich połączeniach
data class BlockWithConnections(
    val id: Int,
    val type: String,
    val connectedBlocks: List<Int?>, // Lista ID bloków połączonych
    val firstValue: String?,         // Dla OperationBlock i IfBlock
    val secondValue: String?,        // Dla OperationBlock i IfBlock
    val thirdValue: String?,         // Dla OperationBlock
    val action: String?,             // Dla wszystkich bloków z polem action
    val InputAction: String?,
    val values: String?,             // Dla InputBlock
    //val inputType: String?,          // Dla InputBlock
    val ifFirstValue: String?,       // Dla IfBlock
    val ifSecondValue: String?,      // Dla IfBlock
    val ifAction: String?,           // Dla IfBlock
    //val lineTrueId: Int?,              // ID bloku połączonego linią true w IfBlock
    //val lineFalseId: Int?,             // ID bloku połączonego linią false w IfBlock
    val blockTrue: Int?,             // ID bloku połączonego w przypadku true w IfBlock
    val blockFalse: Int?             // ID bloku połączonego w przypadku false w IfBlock
)

