import java.io.File
import java.awt.Image
import java.awt.Toolkit
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JButton
import java.awt.BorderLayout
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import kotlin.system.exitProcess
import javax.swing.SwingUtilities

class ImageLabeler(inputFolder: String, private val outputFolder: String) : JFrame() {
    private val imageFiles = File(inputFolder).listFiles { file -> file.extension == "jpg" }?.sorted()
    private var currentIndex = 0
    private val imageLabel = JLabel()

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout()
        add(createImagePanel(), BorderLayout.CENTER)
        add(createButtonPanel(), BorderLayout.SOUTH)
        pack()
        setLocationRelativeTo(null)
        isVisible = true
    }

    private fun createImagePanel(): JPanel {
        val panel = JPanel(BorderLayout())
        panel.add(imageLabel, BorderLayout.CENTER)
        loadImage()
        return panel
    }

    private fun createButtonPanel(): JPanel {
        val panel = JPanel()
        val catButton = JButton("Cat")
        catButton.preferredSize = Dimension(100, 50)
        catButton.addActionListener { onButtonClick("cat") }
        panel.add(catButton)

        val dogButton = JButton("Dog")
        dogButton.preferredSize = Dimension(100, 50)
        dogButton.addActionListener { onButtonClick("dog") }
        panel.add(dogButton)

        return panel
    }

    private fun loadImage() {
        if (currentIndex < (imageFiles?.size ?: 0)) {
            val image = ImageIO.read(imageFiles!![currentIndex])
            val scaledImage = scaleImage(image)
            imageLabel.icon = ImageIcon(scaledImage)
            imageLabel.preferredSize = Dimension(scaledImage.getWidth(null), scaledImage.getHeight(null))
            setTitle("${imageFiles[currentIndex].name} (${imageFiles.size - currentIndex} Remaining)")
            pack()
        } else {
            exitProcess(0)
        }
    }

    private fun scaleImage(image: Image): Image {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val maxWidth = (screenSize.width * 0.8).toInt()
        val maxHeight = (screenSize.height * 0.8).toInt()
        val aspectRatio = image.getWidth(null).toDouble() / image.getHeight(null)

        val scaledWidth = if (image.getWidth(null) > maxWidth) maxWidth else image.getWidth(null)
        val scaledHeight = (scaledWidth / aspectRatio).toInt()

        return if (scaledHeight > maxHeight) {
            val adjustedWidth = (maxHeight * aspectRatio).toInt()
            image.getScaledInstance(adjustedWidth, maxHeight, Image.SCALE_SMOOTH)
        } else {
            image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH)
        }
    }

    private fun onButtonClick(label: String) {
        val currentFile = imageFiles!![currentIndex]
        val outputFile = File(outputFolder, "$label.${currentFile.name}")
        currentFile.renameTo(outputFile)
        currentIndex++
        loadImage()
    }
}

fun main() {
    val inputFolder = "C:\\Users\\Alexander\\Documents\\School Work\\Stevens Institute of Technology\\2. Second Semester\\Data Acquisition, Modeling and Analysis - Deep Learning\\Final Project\\Unlabeled"
    val outputFolder = "C:\\Users\\Alexander\\Documents\\School Work\\Stevens Institute of Technology\\2. Second Semester\\Data Acquisition, Modeling and Analysis - Deep Learning\\Final Project\\Labeled"
    SwingUtilities.invokeLater { ImageLabeler(inputFolder, outputFolder) }
}