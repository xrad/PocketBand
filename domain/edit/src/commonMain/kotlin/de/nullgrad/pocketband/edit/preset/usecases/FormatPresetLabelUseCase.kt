package de.nullgrad.pocketband.edit.preset.usecases

class FormatPresetLabelUseCase {
    operator fun invoke(id: Int, name: String) : String {
        return "%02d $name".format(id)
    }
}